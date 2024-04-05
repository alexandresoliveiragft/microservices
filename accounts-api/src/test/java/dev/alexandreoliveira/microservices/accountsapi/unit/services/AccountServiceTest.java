package dev.alexandreoliveira.microservices.accountsapi.unit.services;

import dev.alexandreoliveira.microservices.accountsapi.database.entities.AccountEntity;
import dev.alexandreoliveira.microservices.accountsapi.database.entities.UserEntity;
import dev.alexandreoliveira.microservices.accountsapi.database.entities.enums.AccountTypeEnum;
import dev.alexandreoliveira.microservices.accountsapi.database.repositories.AccountRepository;
import dev.alexandreoliveira.microservices.accountsapi.database.repositories.UserRepository;
import dev.alexandreoliveira.microservices.accountsapi.dtos.AccountDTO;
import dev.alexandreoliveira.microservices.accountsapi.mappers.AccountMapper;
import dev.alexandreoliveira.microservices.accountsapi.mappers.AccountMapperImpl;
import dev.alexandreoliveira.microservices.accountsapi.mappers.UserMapper;
import dev.alexandreoliveira.microservices.accountsapi.services.AccountServiceImpl;
import dev.alexandreoliveira.microservices.accountsapi.services.exceptions.ServiceException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.ResourceUtils;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AccountServiceTest {

    @Mock
    AccountRepository mockAccountRepository;

    @Mock
    UserRepository mockUserRepository;

    AutoCloseable autoCloseable;

    @BeforeEach
    void beforeEach() {
        autoCloseable = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void afterEach() throws Exception {
        Mockito.clearInvocations(mockAccountRepository, mockUserRepository);
        Mockito.reset(mockAccountRepository, mockUserRepository);
        autoCloseable.close();
    }

    @Test
    @Order(1)
    void shouldExpectedExceptionWhenUserNotFound() {
        Mockito.doReturn(Optional.empty()).when(mockUserRepository).findById(0L);

        AccountMapper accountMapper = Mappers.getMapper(AccountMapper.class);

        var sut = new AccountServiceImpl(
                mockUserRepository,
                mockAccountRepository,
                accountMapper
        );

        var fakeAccount = new AccountDTO();
        fakeAccount.setUserId(0L);
        fakeAccount.setAccountType(AccountTypeEnum.PF.name());

        ServiceException serviceException = Assertions.assertThrows(
                ServiceException.class,
                () -> sut.createAccount(fakeAccount),
                "Expected an error occurs here!"
        );

        org.assertj.core.api.Assertions.assertThat(serviceException.getMessage()).isEqualTo("User not found");
    }

    @Test
    @Order(2)
    void shouldExpectedAValidData() {
        var fakeUser = new UserEntity();
        fakeUser.setId(0L);
        fakeUser.setName("Fake");
        fakeUser.setEmail("fake@email.com");
        fakeUser.setMobileNumber("01901010101");
        fakeUser.setCreatedAt(LocalDateTime.now());
        fakeUser.setCreatedBy("test");

        Mockito.doReturn(Optional.of(fakeUser)).when(mockUserRepository).findById(0L);

        var expectedFakeAccount = new AccountEntity();
        expectedFakeAccount.setId(0L);
        expectedFakeAccount.setAccountNumber("0101101001");
        expectedFakeAccount.setAccountType(AccountTypeEnum.PF);
        expectedFakeAccount.setUser(fakeUser);

        var toSaveAccount = new AccountEntity();
        toSaveAccount.setAccountType(AccountTypeEnum.PF);
        toSaveAccount.setUser(fakeUser);

        Mockito.doReturn(expectedFakeAccount).when(mockAccountRepository).save(toSaveAccount);

        var accountMapper = Mappers.getMapper(AccountMapper.class);
        var userMapper = Mappers.getMapper(UserMapper.class);

        ReflectionTestUtils.setField(accountMapper, "userMapper", userMapper);

        var sut = new AccountServiceImpl(
                mockUserRepository,
                mockAccountRepository,
                accountMapper
        );

        var fakeAccount = new AccountDTO();
        fakeAccount.setUserId(0L);
        fakeAccount.setAccountType(AccountTypeEnum.PF.name());

        AccountDTO account = sut.createAccount(fakeAccount);

        org.assertj.core.api.Assertions.assertThat(account).isNotNull();
        org.assertj.core.api.Assertions.assertThat(account.getAccountNumber()).isEqualTo("0101101001");
    }
}
