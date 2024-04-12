package dev.alexandreoliveira.microservices.accountsapi.services;

import dev.alexandreoliveira.microservices.accountsapi.controllers.data.accounts.AccountControllerCreateRequest;
import dev.alexandreoliveira.microservices.accountsapi.dtos.AccountDto;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public interface AccountService {

    @Transactional(rollbackFor = {Throwable.class})
    AccountDto createAccount(AccountControllerCreateRequest request);
}
