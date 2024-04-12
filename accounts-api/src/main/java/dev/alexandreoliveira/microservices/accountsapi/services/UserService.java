package dev.alexandreoliveira.microservices.accountsapi.services;

import dev.alexandreoliveira.microservices.accountsapi.controllers.data.users.UserControllerCreateRequest;
import dev.alexandreoliveira.microservices.accountsapi.controllers.data.users.UserControllerIndexRequest;
import dev.alexandreoliveira.microservices.accountsapi.controllers.data.users.UserControllerUpdateRequest;
import dev.alexandreoliveira.microservices.accountsapi.dtos.UserDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Transactional(readOnly = true)
public interface UserService {

    @Transactional(rollbackFor = {Throwable.class})
    UserDto createUser(UserControllerCreateRequest request);

    UserDto find(UUID id);

    UserDto update(UserControllerUpdateRequest request);

    Page<UserDto> index(UserControllerIndexRequest request, Pageable pageable);
}
