package dev.alexandreoliveira.microservices.accountsapi.mappers;

import dev.alexandreoliveira.microservices.accountsapi.controllers.data.users.UserControllerCreateRequest;
import dev.alexandreoliveira.microservices.accountsapi.controllers.data.users.UserControllerIndexRequest;
import dev.alexandreoliveira.microservices.accountsapi.database.entities.UserEntity;
import dev.alexandreoliveira.microservices.accountsapi.dtos.AccountDto;
import dev.alexandreoliveira.microservices.accountsapi.dtos.UserDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(source = "name", target = "name")
    @Mapping(source = "email", target = "email")
    @Mapping(source = "mobileNumber", target = "mobileNumber")
    UserDto toDto(UserControllerCreateRequest request);

    @Mapping(source = "name", target = "name")
    @Mapping(source = "email", target = "email")
    @Mapping(source = "mobileNumber", target = "mobileNumber")
    UserEntity toEntity(UserDto dto);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "email", target = "email")
    @Mapping(source = "mobileNumber", target = "mobileNumber")
    UserDto toDto(UserEntity entity);

    @Mapping(source = "userId", target = "id")
    @Named("toEntityByAccount")
    UserEntity toEntityByAccount(AccountDto dto);

    @Mapping(source = "name", target = "name")
    @Mapping(source = "email", target = "email")
    @Mapping(source = "mobileNumber", target = "mobileNumber")
    UserEntity toEntity(UserControllerIndexRequest request);
}
