package dev.alexandreoliveira.microservices.accountsapi.unit.mappers;

import dev.alexandreoliveira.microservices.accountsapi.controllers.data.accounts.AccountControllerCreateRequest;
import dev.alexandreoliveira.microservices.accountsapi.database.entities.AccountEntity;
import dev.alexandreoliveira.microservices.accountsapi.database.entities.UserEntity;
import dev.alexandreoliveira.microservices.accountsapi.database.entities.enums.AccountTypeEnum;
import dev.alexandreoliveira.microservices.accountsapi.dtos.AccountDTO;
import dev.alexandreoliveira.microservices.accountsapi.mappers.AccountMapper;
import dev.alexandreoliveira.microservices.accountsapi.mappers.UserMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mapstruct.factory.Mappers;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.stream.Stream;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AccountMapperTest {

    final AccountMapper accountMapper = Mappers.getMapper(AccountMapper.class);
    final UserMapper userMapper = Mappers.getMapper(UserMapper.class);

    @Test
    @Order(1)
    void shouldExpectedToDTOMethodReturnNullWhenRequestDataIsInvalid() {
        AccountDTO dto = accountMapper.toDTO(null);
        Assertions.assertThat(dto).isNull();
    }

    @Test
    @Order(2)
    void shouldExpectedToDTOMethodReturnDtoWhenRequestDataIsCorrect() {
        var request = new AccountControllerCreateRequest(
                1L,
                AccountTypeEnum.PF.name()
        );
        AccountDTO dto = accountMapper.toDTO(request);
        Assertions.assertThat(dto).isNotNull();
        Assertions.assertThat(dto.getUserId()).isPositive();
        Assertions.assertThat(dto.getAccountType()).isNotBlank();
    }

    @Test
    @Order(3)
    void shouldExpectedToEntityMethodReturnNullWhenDtoDataIsInvalid() {
        AccountEntity entity = accountMapper.toEntity(null);
        Assertions.assertThat(entity).isNull();
    }

    @Test
    @Order(4)
    void shouldExpectedToEntityMethodReturnEntityWhenDtoDataIsCorrect() {
        var dto = new AccountDTO();
        dto.setAccountType(AccountTypeEnum.PJ.name());
        dto.setUserId(1L);
        dto.setId(1L);
        dto.setAccountNumber("0010020034");
        ReflectionTestUtils.setField(accountMapper, "userMapper", userMapper);
        AccountEntity entity = accountMapper.toEntity(dto);
        Assertions.assertThat(entity).isNotNull();
        Assertions.assertThat(entity.getId()).isPositive();
        Assertions.assertThat(entity.getAccountNumber()).isNotBlank();
        Assertions.assertThat(entity.getUser()).isNotNull();
        Assertions.assertThat(entity.getUser().getId()).isPositive();
    }

    @Test
    @Order(5)
    void shouldExpectedToDtoMethodReturnNullWhenEntityDataIsInvalid() {
        AccountDTO dto = accountMapper.toDto(null);
        Assertions.assertThat(dto).isNull();
    }

    @Test
    @Order(6)
    void shouldExpectedToDtoMethodReturnDtoWhenEntityDataIsCorrect() {
        var user = new UserEntity();
        user.setId(1L);
        var account = new AccountEntity();
        account.setAccountType(AccountTypeEnum.PJ);
        account.setUser(user);
        account.setId(1L);
        account.setAccountNumber("0010020034");
        ReflectionTestUtils.setField(accountMapper, "userMapper", userMapper);
        AccountDTO dto = accountMapper.toDto(account);
        Assertions.assertThat(dto).isNotNull();
        Assertions.assertThat(dto.getId()).isPositive();
        Assertions.assertThat(dto.getAccountNumber()).isNotBlank();
        Assertions.assertThat(dto.getUserId()).isPositive();
    }

    @Test
    @Order(7)
    void shouldExpectedToTransformAccountTypeMethodReturnNullWhenAccountTypeDataIsInvalid() {
        AccountTypeEnum accountType = accountMapper.toTransformAccountType(null);
        Assertions.assertThat(accountType).isNull();
    }

    @Test
    @Order(8)
    void shouldExpectedToTransformAccountTypeMethodReturnExceptionWhenAccountTypeDataIsInvalid() {
        IllegalArgumentException illegalArgumentException = org.junit.jupiter.api.Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> accountMapper.toTransformAccountType("INVALID_TYPE"),
                "Expected an error"
        );
        Assertions.assertThat(illegalArgumentException.getMessage()).isEqualTo("Unexpected enum constant: INVALID_TYPE");
    }

    @ParameterizedTest
    @MethodSource("shouldExpectedToTransformAccountTypeMethodReturnAccountTypeWhenAccountTypeDataIsCorrectSource")
    @Order(9)
    void shouldExpectedToTransformAccountTypeMethodReturnAccountTypeWhenAccountTypeDataIsCorrect(AccountTypeEnum accountTypeEnum) {
        AccountTypeEnum accountType = accountMapper.toTransformAccountType(accountTypeEnum.name());
        Assertions.assertThat(accountType).isEqualTo(accountTypeEnum);
    }

    static Stream<AccountTypeEnum> shouldExpectedToTransformAccountTypeMethodReturnAccountTypeWhenAccountTypeDataIsCorrectSource() {
        return Stream.of(AccountTypeEnum.values());
    }
}
