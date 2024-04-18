package dev.alexandreoliveira.microservices.accountsapi.unit.services;

import dev.alexandreoliveira.microservices.accountsapi.controllers.data.accounts.AccountControllerCreateRequest;
import dev.alexandreoliveira.microservices.accountsapi.database.entities.AccountEntity;
import dev.alexandreoliveira.microservices.accountsapi.database.entities.UserEntity;
import dev.alexandreoliveira.microservices.accountsapi.database.entities.enums.AccountTypeEnum;
import dev.alexandreoliveira.microservices.accountsapi.database.repositories.AccountsRepository;
import dev.alexandreoliveira.microservices.accountsapi.database.repositories.UsersRepository;
import dev.alexandreoliveira.microservices.accountsapi.dtos.AccountDto;
import dev.alexandreoliveira.microservices.accountsapi.mappers.AccountMapper;
import dev.alexandreoliveira.microservices.accountsapi.mappers.UserMapper;
import dev.alexandreoliveira.microservices.accountsapi.services.AccountService;
import dev.alexandreoliveira.microservices.accountsapi.exceptions.ServiceException;
import dev.alexandreoliveira.microservices.accountsapi.unit.UnitTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

class AccountServiceTest extends UnitTest {

    @Mock
    AccountsRepository mockAccountsRepository;

    @Mock
    UsersRepository mockUsersRepository;

    AutoCloseable autoCloseable;

    @BeforeEach
    void beforeEach() {
        autoCloseable = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void afterEach() throws Exception {
        Mockito.clearInvocations(mockAccountsRepository, mockUsersRepository);
        Mockito.reset(mockAccountsRepository, mockUsersRepository);
        autoCloseable.close();
    }

    @Test
    @Order(1)
    void shouldExpectedExceptionWhenUserNotFound() {
        UUID userId = UUID.randomUUID();

        Mockito.doReturn(Optional.empty()).when(mockUsersRepository).findById(userId);

        AccountMapper accountMapper = Mappers.getMapper(AccountMapper.class);

        var sut = new AccountService(
                mockUsersRepository,
                mockAccountsRepository,
                accountMapper
        );

        var fakeAccount = new AccountControllerCreateRequest(
                userId,
                AccountTypeEnum.PF.name()
        );

        ServiceException serviceException = Assertions.assertThrows(
                ServiceException.class,
                () -> sut.create(fakeAccount),
                "Expected an error occurs here!"
        );

        org.assertj.core.api.Assertions.assertThat(serviceException.getMessage()).isEqualTo("User not found");
    }

    @Test
    @Order(2)
    void shouldExpectedAValidData() {
        UUID accountId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        var fakeUser = new UserEntity();
        fakeUser.setId(userId);
        fakeUser.setName("Fake");
        fakeUser.setEmail("fake@email.com");
        fakeUser.setMobileNumber("01901010101");
        fakeUser.setCreatedAt(LocalDateTime.now());
        fakeUser.setCreatedBy("test");

        Mockito.doReturn(Optional.of(fakeUser)).when(mockUsersRepository).findById(userId);

        var expectedFakeAccount = new AccountEntity();
        expectedFakeAccount.setId(accountId);
        expectedFakeAccount.setAccountNumber("0101101001");
        expectedFakeAccount.setAccountType(AccountTypeEnum.PF);
        expectedFakeAccount.setUser(fakeUser);

        var toSaveAccount = new AccountEntity();
        toSaveAccount.setAccountType(AccountTypeEnum.PF);
        toSaveAccount.setUser(fakeUser);

        Mockito.doReturn(expectedFakeAccount).when(mockAccountsRepository).save(toSaveAccount);

        var accountMapper = Mappers.getMapper(AccountMapper.class);
        var userMapper = Mappers.getMapper(UserMapper.class);

        ReflectionTestUtils.setField(accountMapper, "userMapper", userMapper);

        var sut = new AccountService(
                mockUsersRepository,
                mockAccountsRepository,
                accountMapper
        );

        var fakeAccount = new AccountControllerCreateRequest(
                userId,
                AccountTypeEnum.PF.name()
        );

        AccountDto account = sut.create(fakeAccount);

        org.assertj.core.api.Assertions.assertThat(account).isNotNull();
        org.assertj.core.api.Assertions.assertThat(account.getAccountNumber()).isEqualTo("0101101001");
    }
}
