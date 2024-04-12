package dev.alexandreoliveira.microservices.accountsapi.unit.mappers;

import dev.alexandreoliveira.microservices.accountsapi.controllers.data.users.UserControllerCreateRequest;
import dev.alexandreoliveira.microservices.accountsapi.database.entities.UserEntity;
import dev.alexandreoliveira.microservices.accountsapi.database.entities.enums.AccountTypeEnum;
import dev.alexandreoliveira.microservices.accountsapi.dtos.AccountDto;
import dev.alexandreoliveira.microservices.accountsapi.dtos.UserDto;
import dev.alexandreoliveira.microservices.accountsapi.mappers.UserMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.UUID;

class UserMapperTest {

    final UserMapper userMapper = Mappers.getMapper(UserMapper.class);

    @Test
    @Order(1)
    void shouldExpectedToDtoMethodReturnNullWhenRequestDataIsInvalid() {
        UserControllerCreateRequest request = null;
        UserDto dto = userMapper.toDto(request);
        Assertions.assertThat(dto).isNull();
    }

    @Test
    @Order(2)
    void shouldExpectedToDtoMethodReturnDtoWhenRequestDataIsCorrect() {
        var request = new UserControllerCreateRequest(
                "fake",
                "fake@email.com",
                "+5531911112222"
        );
        UserDto dto = userMapper.toDto(request);
        Assertions.assertThat(dto).isNotNull();
        Assertions.assertThat(dto.getName()).isNotBlank();
        Assertions.assertThat(dto.getEmail()).isNotBlank();
        Assertions.assertThat(dto.getMobileNumber()).isNotBlank();
    }

    @Test
    @Order(3)
    void shouldExpectedToEntityMethodReturnNullWhenDtoDataIsInvalid() {
        UserDto userDto = null;
        UserEntity entity = userMapper.toEntity(userDto);
        Assertions.assertThat(entity).isNull();
    }

    @Test
    @Order(4)
    void shouldExpectedToEntityMethodReturnDtoWhenDtoDataIsCorrect() {
        var accountDto = new AccountDto();
        accountDto.setId(UUID.randomUUID());
        accountDto.setAccountNumber("0001000200034");
        accountDto.setAccountType(AccountTypeEnum.PF.name());
        accountDto.setUserId(UUID.randomUUID());
        var dto = new UserDto();
        dto.setId(UUID.randomUUID());
        dto.setName("Fake");
        dto.setEmail("fake@email.com");
        dto.setMobileNumber("+5531911112222");
        dto.setAccounts(List.of(accountDto));
        UserEntity entity = userMapper.toEntity(dto);
        Assertions.assertThat(entity).isNotNull();
        Assertions.assertThat(entity.getId()).isNotNull();
        Assertions.assertThat(entity.getName()).isNotBlank();
        Assertions.assertThat(entity.getEmail()).isNotBlank();
        Assertions.assertThat(entity.getMobileNumber()).isNotBlank();
        Assertions.assertThat(entity.getAccounts()).isNotEmpty();
    }
}
