package dev.alexandreoliveira.microservices.accountsapi.mappers;

import dev.alexandreoliveira.microservices.accountsapi.controllers.data.users.UserControllerCreateRequest;
import dev.alexandreoliveira.microservices.accountsapi.controllers.data.users.UserControllerIndexRequest;
import dev.alexandreoliveira.microservices.accountsapi.database.entities.UserEntity;
import dev.alexandreoliveira.microservices.accountsapi.dtos.AccountDto;
import dev.alexandreoliveira.microservices.accountsapi.dtos.UserDto;
import dev.alexandreoliveira.microservices.accountsapi.exceptions.MapperException;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring", unexpectedValueMappingException = MapperException.class)
public interface UserMapper {

    UserDto toDto(UserControllerCreateRequest request);

    UserEntity toEntity(UserDto dto);

    UserDto toDto(UserEntity entity);

    UserDto toDtoComplete(UserEntity entity);

    @Mapping(source = "userId", target = "id")
    @Named("toEntityByAccount")
    UserEntity toEntityByAccount(AccountDto dto);

    UserEntity toEntity(UserControllerIndexRequest request);
}
