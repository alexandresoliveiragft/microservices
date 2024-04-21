package dev.alexandreoliveira.microservices.employeesapi.services;

import dev.alexandreoliveira.microservices.employeesapi.controllers.request.employees.EmployeesControllerCreateRequest;
import dev.alexandreoliveira.microservices.employeesapi.databases.entities.AddressEntity;
import dev.alexandreoliveira.microservices.employeesapi.databases.repositories.AddressRepository;
import dev.alexandreoliveira.microservices.employeesapi.dtos.AddressDto;
import dev.alexandreoliveira.microservices.employeesapi.mappers.AddressMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AddressesService {

    private final AddressRepository addressRepository;
    private final AddressMapper addressMapper;

    public AddressDto create(UUID employeeId, LocalDateTime employeeVersion, EmployeesControllerCreateRequest.EmployeesControllerEmployeeAddressRequest addressRequest) {
        AddressEntity entity = addressMapper.toEntity(employeeId, employeeVersion, addressRequest);
        AddressEntity savedAddress = addressRepository.save(entity);
        return addressMapper.toDto(savedAddress);
    }
}
