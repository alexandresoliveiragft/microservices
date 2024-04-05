package dev.alexandreoliveira.microservices.accountsapi.services;

import dev.alexandreoliveira.microservices.accountsapi.dtos.UserDTO;

public interface UserService {

    UserDTO createUser(UserDTO dto);

    UserDTO find(Long id);
}
