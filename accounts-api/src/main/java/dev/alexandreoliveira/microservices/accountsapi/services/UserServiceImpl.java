package dev.alexandreoliveira.microservices.accountsapi.services;

import dev.alexandreoliveira.microservices.accountsapi.database.entities.UserEntity;
import dev.alexandreoliveira.microservices.accountsapi.database.repositories.UserRepository;
import dev.alexandreoliveira.microservices.accountsapi.dtos.UserDTO;
import dev.alexandreoliveira.microservices.accountsapi.mappers.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserDTO createUser(UserDTO dto) {
        UserEntity userEntity = userMapper.toEntity(dto);
        UserEntity userSaved = userRepository.save(userEntity);
        return userMapper.toDto(userSaved);
    }

    @Override
    public UserDTO find(Long id) {
        UserEntity userFound = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        return userMapper.toDto(userFound);
    }
}
