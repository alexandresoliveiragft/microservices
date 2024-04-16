package dev.alexandreoliveira.microservices.accountsapi.services;

import dev.alexandreoliveira.microservices.accountsapi.controllers.data.users.UserControllerCreateRequest;
import dev.alexandreoliveira.microservices.accountsapi.controllers.data.users.UserControllerIndexRequest;
import dev.alexandreoliveira.microservices.accountsapi.controllers.data.users.UserControllerUpdateRequest;
import dev.alexandreoliveira.microservices.accountsapi.database.entities.UserEntity;
import dev.alexandreoliveira.microservices.accountsapi.database.repositories.UsersRepository;
import dev.alexandreoliveira.microservices.accountsapi.dtos.UserDto;
import dev.alexandreoliveira.microservices.accountsapi.helpers.StringHelper;
import dev.alexandreoliveira.microservices.accountsapi.helpers.ValidationHelper;
import dev.alexandreoliveira.microservices.accountsapi.mappers.UserMapper;
import dev.alexandreoliveira.microservices.accountsapi.services.exceptions.ServiceException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UsersRepository usersRepository;
    private final UserMapper userMapper;
    private final StringHelper stringHelper;

    @Transactional(rollbackFor = {Throwable.class})
    public UserDto create(UserControllerCreateRequest request) {
        if (usersRepository.findByEmailIgnoreCaseOrMobileNumber(request.email(), request.mobileNumber()).isPresent()) {
            throw new ServiceException("This email / mobileNumber exists");
        }

        UserDto dto = userMapper.toDto(request);
        UserEntity userEntity = userMapper.toEntity(dto);
        UserEntity userSaved = usersRepository.save(userEntity);
        return userMapper.toDto(userSaved);
    }

    @Transactional(readOnly = true)
    public UserDto show(UUID id) {
        UserEntity userFound = usersRepository
                .findById(id)
                .orElseThrow(() -> new ServiceException("User not found"));
        return userMapper.toDto(userFound);
    }

    @Transactional(rollbackFor = {Throwable.class})
    public UserDto update(UserControllerUpdateRequest request) {
        UserEntity entity = usersRepository
                .findById(request.id())
                .orElseThrow(() -> new ServiceException("User not found"));
        entity.setName(stringHelper.requiredNonBlankOrElse(request.name(), entity.getName()));
        entity.setEmail(stringHelper.requiredNonBlankOrElse(request.email(), entity.getEmail()));
        entity.setMobileNumber(stringHelper.requiredNonBlankOrElse(request.mobileNumber(), entity.getMobileNumber()));
        usersRepository.save(entity);
        return userMapper.toDto(entity);
    }

    @Transactional(readOnly = true)
    public Page<UserDto> index(UserControllerIndexRequest request, boolean isComplete, Pageable pageable) {
        if (ValidationHelper.isAllNull(request)) {
            throw new ServiceException("All parameters are null, please add some parameter to validate your search.");
        }

        ExampleMatcher exampleMatcher = ExampleMatcher
                .matchingAll()
                .withIgnoreNullValues()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
                .withIgnoreCase(true);

        UserEntity exampleUserEntity = Objects.requireNonNullElse(
                userMapper.toEntity(request),
                new UserEntity());

        Example<UserEntity> entityExample = Example.of(exampleUserEntity, exampleMatcher);

        Specification<UserEntity> where = usersRepository.where(request, entityExample);

        Page<UserEntity> users = usersRepository.findAll(where, pageable);

        return users.map(entity -> isComplete ? userMapper.toDtoComplete(entity) : userMapper.toDto(entity));
    }
}
