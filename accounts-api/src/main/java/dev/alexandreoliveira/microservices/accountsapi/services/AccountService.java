package dev.alexandreoliveira.microservices.accountsapi.services;

import dev.alexandreoliveira.microservices.accountsapi.controllers.data.accounts.AccountControllerCreateRequest;
import dev.alexandreoliveira.microservices.accountsapi.database.entities.AccountEntity;
import dev.alexandreoliveira.microservices.accountsapi.database.entities.UserEntity;
import dev.alexandreoliveira.microservices.accountsapi.database.repositories.AccountsRepository;
import dev.alexandreoliveira.microservices.accountsapi.database.repositories.UsersRepository;
import dev.alexandreoliveira.microservices.accountsapi.dtos.AccountDto;
import dev.alexandreoliveira.microservices.accountsapi.exceptions.ServiceException;
import dev.alexandreoliveira.microservices.accountsapi.mappers.AccountMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final UsersRepository usersRepository;
    private final AccountsRepository accountsRepository;
    private final AccountMapper accountMapper;

    @Transactional(rollbackFor = {Throwable.class})
    public AccountDto create(@Valid AccountControllerCreateRequest request) {
        UserEntity foundUserEntity = usersRepository
                .findById(request.userId())
                .orElseThrow(() -> new ServiceException("User not found"));
        AccountDto dto = accountMapper.toDto(request);
        AccountEntity entity = accountMapper.toEntity(dto);
        entity.setUser(foundUserEntity);
        entity.setIsEnabled(Boolean.TRUE);
        AccountEntity savedEntity = accountsRepository.save(entity);
        return accountMapper.toDto(savedEntity);
    }

    @Transactional(readOnly = true)
    public AccountDto show(UUID id) {
        AccountEntity account = accountsRepository
                .findById(id)
                .orElseThrow(() -> new ServiceException("Account not found"));
        account.getUser();
        return accountMapper.toDto(account);
    }

    @Transactional(rollbackFor = {Throwable.class})
    public void delete(UUID id) {
        AccountEntity account = accountsRepository
                .findById(id)
                .orElseThrow(() -> new ServiceException("Account not found"));
        accountsRepository.delete(account);
    }
}
