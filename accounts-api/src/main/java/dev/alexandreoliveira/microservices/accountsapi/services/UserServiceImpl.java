package dev.alexandreoliveira.microservices.accountsapi.services;

import dev.alexandreoliveira.microservices.accountsapi.controllers.data.users.UserControllerCreateRequest;
import dev.alexandreoliveira.microservices.accountsapi.database.entities.UserEntity;
import dev.alexandreoliveira.microservices.accountsapi.database.repositories.UserRepository;
import dev.alexandreoliveira.microservices.accountsapi.dtos.UserDTO;
import dev.alexandreoliveira.microservices.accountsapi.mappers.UserMapper;
import dev.alexandreoliveira.microservices.accountsapi.services.exceptions.ServiceException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserDTO createUser(UserControllerCreateRequest request) {
        if (userRepository.findByEmailIgnoreCaseOrMobileNumber(request.email(), request.mobileNumber()).isPresent()) {
            throw new ServiceException("This email / mobileNumber exists");
        }

        UserDTO dto = userMapper.toDto(request);
        UserEntity userEntity = userMapper.toEntity(dto);
        UserEntity userSaved = userRepository.save(userEntity);
        return userMapper.toDto(userSaved);
    }

    @Override
    public UserDTO find(UUID id) {
        UserEntity userFound = userRepository.findById(id).orElseThrow(() -> new ServiceException("User not found"));
        return userMapper.toDto(userFound);
    }
}
