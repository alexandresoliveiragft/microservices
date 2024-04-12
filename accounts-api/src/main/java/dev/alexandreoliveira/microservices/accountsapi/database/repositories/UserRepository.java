package dev.alexandreoliveira.microservices.accountsapi.database.repositories;

import dev.alexandreoliveira.microservices.accountsapi.database.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, UUID> {

    Optional<UserEntity> findByEmailIgnoreCaseOrMobileNumber(String email, String mobileNumber);
}
