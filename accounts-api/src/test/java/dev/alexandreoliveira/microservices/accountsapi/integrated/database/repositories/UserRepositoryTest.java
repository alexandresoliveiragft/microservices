package dev.alexandreoliveira.microservices.accountsapi.integrated.database.repositories;

import dev.alexandreoliveira.microservices.accountsapi.database.entities.UserEntity;
import dev.alexandreoliveira.microservices.accountsapi.database.repositories.UserRepository;
import dev.alexandreoliveira.microservices.accountsapi.integrated.database.helpers.PostgreSQLHelperTest;
import org.assertj.core.api.Assertions;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.AuditorAware;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Locale;
import java.util.Optional;
import java.util.stream.Stream;

import static org.springframework.transaction.annotation.Propagation.NOT_SUPPORTED;

class UserRepositoryTest extends PostgreSQLHelperTest {

    @Autowired
    UserRepository userRepository;

    @Autowired
    PlatformTransactionManager platformTransactionManager;

    @AfterEach
    void afterEach() {
        userRepository.deleteAll();
    }

    @Test
    @Order(1)
    void shouldExpectedToSaveAnUser() {
        var user = new UserEntity();
        user.setName("Fake User");
        user.setEmail("fake-user@email.com");
        user.setMobileNumber("+5531911112222");

        UserEntity savedUser = userRepository.save(user);

        Assertions.assertThat(savedUser).isNotNull();
        Assertions.assertThat(savedUser.getId()).isPositive();
        Assertions.assertThat(savedUser.getVersion()).isNotNull();
        Assertions.assertThat(savedUser.getCreatedAt()).isNotNull();
        Assertions.assertThat(savedUser.getCreatedBy()).isNotBlank();
        Assertions.assertThat(savedUser.getUpdatedAt()).isNotNull();
        Assertions.assertThat(savedUser.getUpdatedBy()).isNotBlank();
    }

    @ParameterizedTest
    @MethodSource("shouldExpectedAnExceptionWhenTryToInsertTheSameDataSource")
    @Order(2)
    @Transactional(propagation = NOT_SUPPORTED)
    void shouldExpectedAnExceptionWhenTryToInsertTheSameData(final UserEntity userError) {
        var user = new UserEntity();
        user.setName("Fake User");
        user.setEmail("fake-user@email.com");
        user.setMobileNumber("+5531911112222");

        userRepository.save(user);

        DataIntegrityViolationException exception = org.junit.jupiter.api.Assertions.assertThrows(
                DataIntegrityViolationException.class,
                () -> new TransactionTemplate(platformTransactionManager).execute(status ->
                        userRepository.save(userError)
                ),
                "Expected an exception"
        );

        Assertions.assertThat(exception).isNotNull();
        Assertions.assertThat(exception.getMessage())
                .containsAnyOf(
                        String.format("(email)=(%s) already exists.", userError.getEmail()),
                        String.format("(mobile_number)=(%s) already exists.", userError.getMobileNumber()));
        Assertions.assertThat(exception.getCause()).isNotNull();
        Assertions.assertThat(exception.getCause()).isInstanceOf(ConstraintViolationException.class);
    }

    static Stream<UserEntity> shouldExpectedAnExceptionWhenTryToInsertTheSameDataSource() {
        var userErrorEmail = new UserEntity();
        userErrorEmail.setName("Fake User");
        userErrorEmail.setEmail("fake-user@email.com");
        userErrorEmail.setMobileNumber("+5531911112223");

        var userErrorMobileNumber = new UserEntity();
        userErrorMobileNumber.setName("Fake User");
        userErrorMobileNumber.setEmail("user@email.com");
        userErrorMobileNumber.setMobileNumber("+5531911112222");

        return Stream.of(
                userErrorEmail,
                userErrorMobileNumber
        );
    }

    @Test
    @Order(3)
    void shouldExpectedToFoundUserWithEmailOrMobileNumber() {
        var fakeUser = new UserEntity();
        fakeUser.setName("Fake User");
        fakeUser.setEmail("fake-user@email.com");
        fakeUser.setMobileNumber("+5531911112222");

        UserEntity savedUser = userRepository.save(fakeUser);

        Assertions.assertThat(savedUser.getId()).isPositive();

        Optional<UserEntity> optionalUserFoundWithEmail = userRepository
                .findByEmailIgnoreCaseOrMobileNumber(fakeUser.getEmail(), "13");

        Assertions.assertThat(optionalUserFoundWithEmail.isPresent()).isTrue();

        Optional<UserEntity> optionalUserFoundWithMobileNumber = userRepository
                .findByEmailIgnoreCaseOrMobileNumber("user@email.com", fakeUser.getMobileNumber());

        Assertions.assertThat(optionalUserFoundWithMobileNumber.isPresent()).isTrue();

        Optional<UserEntity> optionalUserFoundWithEmailToUpperCase = userRepository
                .findByEmailIgnoreCaseOrMobileNumber(
                        fakeUser.getEmail().toLowerCase(Locale.ROOT),
                        "5");

        Assertions.assertThat(optionalUserFoundWithEmailToUpperCase.isPresent()).isTrue();
    }

    @Test
    @Order(4)
    void shouldExpectedToFoundUserById() {
        var fakeUser = new UserEntity();
        fakeUser.setName("Fake User");
        fakeUser.setEmail("fake-user@email.com");
        fakeUser.setMobileNumber("+5531911112222");

        UserEntity savedUser = userRepository.save(fakeUser);

        Assertions.assertThat(savedUser.getId()).isPositive();

        Optional<UserEntity> optionalUserFound = userRepository.findById(savedUser.getId());

        Assertions.assertThat(optionalUserFound.isPresent()).isTrue();

        Optional<UserEntity> optionalUserNotFound = userRepository.findById(-1L);

        Assertions.assertThat(optionalUserNotFound.isPresent()).isFalse();
    }

    @TestConfiguration
    static class UserRepositoryTestConfiguration {

        @Bean
        public AuditorAware<String> auditorAware() {
            return () -> Optional.of("user test");
        }
    }
}
