package dev.alexandreoliveira.microservices.accountsapi.unit.services;

import dev.alexandreoliveira.microservices.accountsapi.database.entities.AccountEntity;
import dev.alexandreoliveira.microservices.accountsapi.database.entities.UserEntity;
import dev.alexandreoliveira.microservices.accountsapi.database.entities.enums.AccountTypeEnum;
import dev.alexandreoliveira.microservices.accountsapi.database.repositories.UserRepository;
import dev.alexandreoliveira.microservices.accountsapi.dtos.UserDTO;
import dev.alexandreoliveira.microservices.accountsapi.mappers.UserMapper;
import dev.alexandreoliveira.microservices.accountsapi.services.UserServiceImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserServiceTest {

    @Mock
    UserRepository mockUserRepository;

    AutoCloseable autoCloseable;

    @BeforeEach
    void beforeEach() {
        autoCloseable = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void afterEach() throws Exception {
        Mockito.clearInvocations(mockUserRepository);
        Mockito.reset(mockUserRepository);
        autoCloseable.close();
    }

    @Test
    @Order(1)
    void shouldExpectedACorrectUser() {
        var fakeUser = new UserEntity();
        fakeUser.setId(1L);
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

        Mockito.when(mockUserRepository.save(toSaveEntity)).thenReturn(fakeUser);

        UserMapper userMapper = Mappers.getMapper(UserMapper.class);

        var sut = new UserServiceImpl(
                mockUserRepository,
                userMapper
        );

        var fakeData = new UserDTO();
        fakeData.setName("Fake");
        fakeData.setEmail("fake@email.com");
        fakeData.setMobileNumber("31911112222");

        UserDTO savedUser = sut.createUser(fakeData);

        Assertions.assertThat(savedUser.getId()).isPositive();
    }

    @Test
    @Order(2)
    void shouldToReturnACorrectUser() {
        var fakeUser = new UserEntity();
        fakeUser.setId(1L);
        fakeUser.setName("Fake");
        fakeUser.setEmail("fake@email.com");
        fakeUser.setMobileNumber("31911112222");
        fakeUser.setCreatedAt(LocalDateTime.now());
        fakeUser.setCreatedBy("test");
        fakeUser.setUpdatedAt(LocalDateTime.now());
        fakeUser.setUpdatedBy("test");
        fakeUser.setVersion(LocalDateTime.now());

        Mockito.when(mockUserRepository.findById(1L)).thenReturn(Optional.of(fakeUser));

        UserMapper userMapper = Mappers.getMapper(UserMapper.class);

        var sut = new UserServiceImpl(
                mockUserRepository,
                userMapper
        );

        UserDTO userDTO = sut.find(1L);

        Assertions.assertThat(userDTO.getEmail()).isNotBlank();
    }

    @Test
    @Order(3)
    void shouldToReturnACorrectUserWithAccount() {
        var fakeAccount = new AccountEntity();
        fakeAccount.setId(1L);
        fakeAccount.setAccountType(AccountTypeEnum.PF);
        fakeAccount.setAccountNumber("0102030405");

        var fakeUser = new UserEntity();
        fakeUser.setId(1L);
        fakeUser.setName("Fake");
        fakeUser.setEmail("fake@email.com");
        fakeUser.setMobileNumber("31911112222");
        fakeUser.setAccounts(List.of(fakeAccount));
        fakeUser.setCreatedAt(LocalDateTime.now());
        fakeUser.setCreatedBy("test");
        fakeUser.setUpdatedAt(LocalDateTime.now());
        fakeUser.setUpdatedBy("test");
        fakeUser.setVersion(LocalDateTime.now());

        Mockito.when(mockUserRepository.findById(1L)).thenReturn(Optional.of(fakeUser));

        UserMapper userMapper = Mappers.getMapper(UserMapper.class);

        var sut = new UserServiceImpl(
                mockUserRepository,
                userMapper
        );

        UserDTO userDTO = sut.find(1L);

        Assertions.assertThat(userDTO.getEmail()).isNotBlank();
        Assertions.assertThat(userDTO.getAccounts()).isNotEmpty();
    }
}
