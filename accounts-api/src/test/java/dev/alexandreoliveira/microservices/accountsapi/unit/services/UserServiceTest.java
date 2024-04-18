package dev.alexandreoliveira.microservices.accountsapi.unit.services;

import dev.alexandreoliveira.microservices.accountsapi.controllers.data.users.UserControllerCreateRequest;
import dev.alexandreoliveira.microservices.accountsapi.database.entities.AccountEntity;
import dev.alexandreoliveira.microservices.accountsapi.database.entities.UserEntity;
import dev.alexandreoliveira.microservices.accountsapi.database.entities.enums.AccountTypeEnum;
import dev.alexandreoliveira.microservices.accountsapi.database.repositories.UsersRepository;
import dev.alexandreoliveira.microservices.accountsapi.dtos.UserDto;
import dev.alexandreoliveira.microservices.accountsapi.helpers.StringHelper;
import dev.alexandreoliveira.microservices.accountsapi.mappers.UserMapper;
import dev.alexandreoliveira.microservices.accountsapi.services.UserService;
import dev.alexandreoliveira.microservices.accountsapi.exceptions.ServiceException;
import dev.alexandreoliveira.microservices.accountsapi.unit.UnitTest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

class UserServiceTest extends UnitTest {

    @Mock
    UsersRepository mockUsersRepository;

    @Mock
    StringHelper stringHelper;

    AutoCloseable autoCloseable;

    @BeforeEach
    void beforeEach() {
        autoCloseable = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void afterEach() throws Exception {
        Mockito.clearInvocations(mockUsersRepository);
        Mockito.reset(mockUsersRepository);
        autoCloseable.close();
    }

    @Test
    @Order(1)
    void shouldExpectedACorrectUser() {
        var fakeUser = new UserEntity();
        fakeUser.setId(UUID.randomUUID());
        fakeUser.setName("Fake");
        fakeUser.setEmail("fake@email.com");
        fakeUser.setMobileNumber("31911112222");
        fakeUser.setCreatedAt(LocalDateTime.now());
        fakeUser.setCreatedBy("test");
        fakeUser.setUpdatedAt(LocalDateTime.now());
        fakeUser.setUpdatedBy("test");
        fakeUser.setVersion(LocalDateTime.now());

        var toSaveEntity = new UserEntity();
        toSaveEntity.setName("Fake");
        toSaveEntity.setEmail("fake@email.com");
        toSaveEntity.setMobileNumber("31911112222");

        Mockito.when(mockUsersRepository.save(toSaveEntity)).thenReturn(fakeUser);

        UserMapper userMapper = Mappers.getMapper(UserMapper.class);

        Mockito.when(stringHelper.requiredNonBlankOrElse(Mockito.anyString(), Mockito.anyString()))
                .thenCallRealMethod();

        var sut = new UserService(
                mockUsersRepository,
                userMapper,
                stringHelper
        );

        var fakeData = new UserControllerCreateRequest(
                "Fake",
                "fake@email.com",
                "31911112222"
        );

        UserDto savedUser = sut.create(fakeData);

        Assertions.assertThat(savedUser.getId()).isNotNull();
    }

    @Test
    @Order(2)
    void shouldToReturnACorrectUser() {
        UUID uuid = UUID.randomUUID();

        var fakeUser = new UserEntity();
        fakeUser.setId(uuid);
        fakeUser.setName("Fake");
        fakeUser.setEmail("fake@email.com");
        fakeUser.setMobileNumber("31911112222");
        fakeUser.setCreatedAt(LocalDateTime.now());
        fakeUser.setCreatedBy("test");
        fakeUser.setUpdatedAt(LocalDateTime.now());
        fakeUser.setUpdatedBy("test");
        fakeUser.setVersion(LocalDateTime.now());

        Mockito.when(mockUsersRepository.findById(uuid)).thenReturn(Optional.of(fakeUser));

        UserMapper userMapper = Mappers.getMapper(UserMapper.class);

        Mockito.when(stringHelper.requiredNonBlankOrElse(Mockito.anyString(), Mockito.anyString()))
                .thenCallRealMethod();

        var sut = new UserService(
                mockUsersRepository,
                userMapper,
                stringHelper
        );

        UserDto userDTO = sut.show(uuid);

        Assertions.assertThat(userDTO.getEmail()).isNotBlank();
    }

    @Test
    @Order(3)
    void shouldToReturnACorrectUserWithAccount() {
        UUID accountId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        var fakeAccount = new AccountEntity();
        fakeAccount.setId(accountId);
        fakeAccount.setAccountType(AccountTypeEnum.PF);
        fakeAccount.setAccountNumber("0102030405");

        var fakeUser = new UserEntity();
        fakeUser.setId(userId);
        fakeUser.setName("Fake");
        fakeUser.setEmail("fake@email.com");
        fakeUser.setMobileNumber("31911112222");
        fakeUser.setAccounts(List.of(fakeAccount));
        fakeUser.setCreatedAt(LocalDateTime.now());
        fakeUser.setCreatedBy("test");
        fakeUser.setUpdatedAt(LocalDateTime.now());
        fakeUser.setUpdatedBy("test");
        fakeUser.setVersion(LocalDateTime.now());

        Mockito.when(mockUsersRepository.findById(userId)).thenReturn(Optional.of(fakeUser));

        UserMapper userMapper = Mappers.getMapper(UserMapper.class);

        Mockito.when(stringHelper.requiredNonBlankOrElse(Mockito.anyString(), Mockito.anyString()))
                .thenCallRealMethod();

        var sut = new UserService(
                mockUsersRepository,
                userMapper,
                stringHelper
        );

        UserDto userDTO = sut.show(userId);

        Assertions.assertThat(userDTO.getEmail()).isNotBlank();
        Assertions.assertThat(userDTO.getAccounts()).isNotEmpty();
    }

    @Test
    @Order(4)
    void shouldExpectedAnExceptionWhenEmailOrMobileNumberExists() {
        var fakeUser = new UserControllerCreateRequest(
                "fake",
                "fake@email.com",
                "31911112222"
        );

        Mockito.when(mockUsersRepository.findByEmailIgnoreCaseOrMobileNumber(fakeUser.email(), fakeUser.mobileNumber()))
                .thenReturn(Optional.of(new UserEntity()));

        Mockito.when(stringHelper.requiredNonBlankOrElse(Mockito.anyString(), Mockito.anyString()))
                .thenCallRealMethod();

        var sut = new UserService(
                mockUsersRepository,
                Mappers.getMapper(UserMapper.class),
                stringHelper
        );

        ServiceException serviceException = org.junit.jupiter.api.Assertions.assertThrows(
                ServiceException.class,
                () -> sut.create(fakeUser),
                "Expected a error occur here");

        Assertions.assertThat(serviceException).isNotNull();
        Assertions.assertThat(serviceException.getMessage()).isNotBlank();
    }
}
