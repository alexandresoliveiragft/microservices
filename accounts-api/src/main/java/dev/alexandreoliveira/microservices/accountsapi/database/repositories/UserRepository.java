package dev.alexandreoliveira.microservices.accountsapi.database.repositories;

import dev.alexandreoliveira.microservices.accountsapi.database.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
}
