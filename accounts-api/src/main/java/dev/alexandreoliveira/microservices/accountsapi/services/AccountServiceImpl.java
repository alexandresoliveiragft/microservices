package dev.alexandreoliveira.microservices.accountsapi.services;

import dev.alexandreoliveira.microservices.accountsapi.database.entities.AccountEntity;
import dev.alexandreoliveira.microservices.accountsapi.database.entities.UserEntity;
import dev.alexandreoliveira.microservices.accountsapi.database.repositories.AccountRepository;
import dev.alexandreoliveira.microservices.accountsapi.database.repositories.UserRepository;
import dev.alexandreoliveira.microservices.accountsapi.dtos.AccountDTO;
import dev.alexandreoliveira.microservices.accountsapi.mappers.AccountMapper;
import dev.alexandreoliveira.microservices.accountsapi.services.exceptions.ServiceException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService{

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;

    @Override
    public AccountDTO createAccount(AccountDTO dto) {
        UserEntity foundUserEntity = userRepository
                .findById(dto.getUserId())
                .orElseThrow(() -> new ServiceException("User not found"));
        AccountEntity entity = accountMapper.toEntity(dto);
        entity.setUser(foundUserEntity);
        AccountEntity savedEntity = accountRepository.save(entity);
        return accountMapper.toDto(savedEntity);
    }
}
