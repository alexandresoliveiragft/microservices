package dev.alexandreoliveira.microservices.accountsapi.database.repositories;

import dev.alexandreoliveira.microservices.accountsapi.database.entities.AccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AccountRepository extends JpaRepository<AccountEntity, UUID> {
}
