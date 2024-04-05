package dev.alexandreoliveira.microservices.accountsapi.services;

import dev.alexandreoliveira.microservices.accountsapi.dtos.AccountDTO;

public interface AccountService {

    AccountDTO createAccount(AccountDTO dto);
}
