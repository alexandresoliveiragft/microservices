package dev.alexandreoliveira.microservices.employeesapi.databases.repositories;

import dev.alexandreoliveira.microservices.employeesapi.databases.entities.AddressEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AddressRepository extends JpaRepository<AddressEntity, UUID> {
}
