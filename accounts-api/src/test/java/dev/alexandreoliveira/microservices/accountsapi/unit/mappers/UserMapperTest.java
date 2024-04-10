package dev.alexandreoliveira.microservices.accountsapi.unit.mappers;

import dev.alexandreoliveira.microservices.accountsapi.controllers.data.users.UserControllerCreateRequest;
import dev.alexandreoliveira.microservices.accountsapi.database.entities.UserEntity;
import dev.alexandreoliveira.microservices.accountsapi.database.entities.enums.AccountTypeEnum;
import dev.alexandreoliveira.microservices.accountsapi.dtos.AccountDTO;
import dev.alexandreoliveira.microservices.accountsapi.dtos.UserDTO;
import dev.alexandreoliveira.microservices.accountsapi.mappers.UserMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.List;

class UserMapperTest {

    final UserMapper userMapper = Mappers.getMapper(UserMapper.class);

    @Test
    @Order(1)
    void shouldExpectedToDtoMethodReturnNullWhenRequestDataIsInvalid() {
        UserControllerCreateRequest request = null;
        UserDTO dto = userMapper.toDto(request);
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
        UserDTO dto = userMapper.toDto(request);
        Assertions.assertThat(dto).isNotNull();
        Assertions.assertThat(dto.getName()).isNotBlank();
        Assertions.assertThat(dto.getEmail()).isNotBlank();
        Assertions.assertThat(dto.getMobileNumber()).isNotBlank();
    }

    @Test
    @Order(3)
    void shouldExpectedToEntityMethodReturnNullWhenDtoDataIsInvalid() {
        UserEntity entity = userMapper.toEntity(null);
        Assertions.assertThat(entity).isNull();
    }

    @Test
    @Order(4)
    void shouldExpectedToEntityMethodReturnDtoWhenDtoDataIsCorrect() {
        var accountDto = new AccountDTO();
        accountDto.setId(1L);
        accountDto.setAccountNumber("0001000200034");
        accountDto.setAccountType(AccountTypeEnum.PF.name());
        accountDto.setUserId(1L);
        var dto = new UserDTO();
        dto.setId(1L);
        dto.setName("Fake");
        dto.setEmail("fake@email.com");
        dto.setMobileNumber("+5531911112222");
        dto.setAccounts(List.of(accountDto));
        UserEntity entity = userMapper.toEntity(dto);
        Assertions.assertThat(entity).isNotNull();
        Assertions.assertThat(entity.getId()).isPositive();
        Assertions.assertThat(entity.getName()).isNotBlank();
        Assertions.assertThat(entity.getEmail()).isNotBlank();
        Assertions.assertThat(entity.getMobileNumber()).isNotBlank();
        Assertions.assertThat(entity.getAccounts()).isNotEmpty();
    }
}
