package dev.alexandreoliveira.microservices.accountsapi.integrated.database.repositories;

import dev.alexandreoliveira.microservices.accountsapi.database.entities.AccountEntity;
import dev.alexandreoliveira.microservices.accountsapi.database.entities.UserEntity;
import dev.alexandreoliveira.microservices.accountsapi.database.entities.enums.AccountTypeEnum;
import dev.alexandreoliveira.microservices.accountsapi.database.repositories.AccountRepository;
import dev.alexandreoliveira.microservices.accountsapi.database.repositories.UserRepository;
import dev.alexandreoliveira.microservices.accountsapi.integrated.database.helpers.PostgreSQLHelperTest;
import org.assertj.core.api.Assertions;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.AuditorAware;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Optional;

import static org.springframework.transaction.annotation.Propagation.NOT_SUPPORTED;

class AccountRepositoryTest extends PostgreSQLHelperTest {

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PlatformTransactionManager platformTransactionManager;

    @AfterEach
    void afterEach() {
        accountRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @Order(1)
    void shouldExpectedAnValidAccount() {
        UserEntity fakeUser = saveFakeUser();

        var fakeAccount = new AccountEntity();
        fakeAccount.setAccountType(AccountTypeEnum.PF);
        fakeAccount.setUser(fakeUser);

        AccountEntity savedAccount = accountRepository.save(fakeAccount);

        Assertions.assertThat(savedAccount).isNotNull();

        Assertions.assertThat(savedAccount).isNotNull();
        Assertions.assertThat(savedAccount.getId()).isPositive();
        Assertions.assertThat(savedAccount.getVersion()).isNotNull();
        Assertions.assertThat(savedAccount.getCreatedAt()).isNotNull();
        Assertions.assertThat(savedAccount.getCreatedBy()).isNotBlank();
        Assertions.assertThat(savedAccount.getUpdatedAt()).isNotNull();
        Assertions.assertThat(savedAccount.getUpdatedBy()).isNotBlank();
        Assertions.assertThat(savedAccount.getAccountNumber()).isNotBlank();
        Assertions.assertThat(savedAccount.getUser()).isNotNull();
    }

    @Test
    @Order(2)
    @Transactional(propagation = NOT_SUPPORTED)
    void shouldExpectedAnExceptionWhenTryToInsertTheSameData() {
        UserEntity fakeUser = saveFakeUser();

        var fakeAccount = new AccountEntity();
        fakeAccount.setAccountType(AccountTypeEnum.PF);
        fakeAccount.setAccountNumber("0010010011");
        fakeAccount.setUser(fakeUser);

        AccountEntity savedAccount = accountRepository.save(fakeAccount);

        Assertions.assertThat(savedAccount.getId()).isPositive();

        DataIntegrityViolationException exception = org.junit.jupiter.api.Assertions.assertThrows(
                DataIntegrityViolationException.class,
                () -> new TransactionTemplate(platformTransactionManager).execute(status -> {
                    var fakeAccountError = new AccountEntity();
                    fakeAccountError.setAccountType(AccountTypeEnum.PJ);
                    fakeAccountError.setAccountNumber("0010010011");
                    fakeAccountError.setUser(fakeUser);

                    return accountRepository.save(fakeAccountError);
                }),
                "Expected an exception"
        );

        Assertions.assertThat(exception).isNotNull();
        Assertions.assertThat(exception.getMessage())
                .containsAnyOf(
                        String.format("(account_number)=(%s) already exists.", fakeAccount.getAccountNumber()));
        Assertions.assertThat(exception.getCause()).isNotNull();
        Assertions.assertThat(exception.getCause()).isInstanceOf(ConstraintViolationException.class);
    }

    private UserEntity saveFakeUser() {
        var user = new UserEntity();
        user.setName("Fake User");
        user.setEmail("fake-user@email.com");
        user.setMobileNumber("+5531911112222");

        return userRepository.save(user);
    }

    @TestConfiguration
    static class AccountRepositoryTestConfiguration {

        @Bean
        public AuditorAware<String> auditorAware() {
            return () -> Optional.of("account test");
        }
    }

}
