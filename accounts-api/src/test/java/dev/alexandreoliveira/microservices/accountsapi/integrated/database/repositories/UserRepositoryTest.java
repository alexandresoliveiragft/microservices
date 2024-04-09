package dev.alexandreoliveira.microservices.accountsapi.integrated.database.repositories;

import dev.alexandreoliveira.microservices.accountsapi.database.entities.UserEntity;
import dev.alexandreoliveira.microservices.accountsapi.database.repositories.UserRepository;
import dev.alexandreoliveira.microservices.accountsapi.integrated.database.helpers.PostgreSQLHelperTest;
import org.assertj.core.api.Assertions;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
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

import java.util.Optional;
import java.util.stream.Stream;

import static org.springframework.transaction.annotation.Propagation.NOT_SUPPORTED;

class UserRepositoryTest extends PostgreSQLHelperTest {

    @Autowired
    UserRepository userRepository;

    @Autowired
    PlatformTransactionManager platformTransactionManager;

    @BeforeEach
    void beforeEach() {
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
        Assertions.assertThat(savedUser.getCreatedBy()).isNotNull();
        Assertions.assertThat(savedUser.getUpdatedAt()).isNotNull();
        Assertions.assertThat(savedUser.getUpdatedBy()).isNotNull();
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

        Exception exception = org.junit.jupiter.api.Assertions.assertThrows(
                Exception.class,
                () -> new TransactionTemplate(platformTransactionManager).execute(status ->
                        userRepository.save(userError)
                ),
                "Expected an exception"
        );

        Assertions.assertThat(exception).isNotNull();
        Assertions.assertThat(exception.getCause()).isNotNull();
    }

    static Stream<UserEntity> shouldExpectedAnExceptionWhenTryToInsertTheSameDataSource() {
        var userErrorEmail = new UserEntity();
        userErrorEmail.setName("Fake User");
        userErrorEmail.setEmail("fake-user@email.com");
        userErrorEmail.setMobileNumber("+5531911112223");

        var userErrorMobileNumber= new UserEntity();
        userErrorMobileNumber.setName("Fake User");
        userErrorMobileNumber.setEmail("user@email.com");
        userErrorMobileNumber.setMobileNumber("+5531911112222");

        return Stream.of(
                userErrorEmail,
                userErrorMobileNumber
        );
    }

    @TestConfiguration
    static class UserRepositoryTestConfiguration {

        @Bean
        public AuditorAware<String> auditorAware() {
            return () -> Optional.of("test");
        }
    }
}
