package dev.alexandreoliveira.microservices.accountsapi.services;

import dev.alexandreoliveira.microservices.accountsapi.controllers.data.users.UserControllerCreateRequest;
import dev.alexandreoliveira.microservices.accountsapi.dtos.UserDTO;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Transactional(readOnly = true)
public interface UserService {

    @Transactional(rollbackFor = {Throwable.class})
    UserDTO createUser(UserControllerCreateRequest request);

    UserDTO find(UUID id);
}
