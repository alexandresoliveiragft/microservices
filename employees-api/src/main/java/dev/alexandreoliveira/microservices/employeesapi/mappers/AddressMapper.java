package dev.alexandreoliveira.microservices.employeesapi.mappers;

import dev.alexandreoliveira.microservices.employeesapi.controllers.request.employees.EmployeesControllerCreateRequest;
import dev.alexandreoliveira.microservices.employeesapi.databases.entities.AddressEntity;
import dev.alexandreoliveira.microservices.employeesapi.dtos.AddressDto;
import org.mapstruct.Mapper;
import org.mapstruct.Named;

import java.util.UUID;

@Mapper(componentModel = "spring")
public interface AddressMapper {

    AddressEntity toEntity(AddressDto dto);

    AddressEntity toEntity(UUID employeeId, EmployeesControllerCreateRequest.EmployeesControllerEmployeeAddressRequest request);

    @Named("addressToEntity")
    AddressEntity toEntity(EmployeesControllerCreateRequest.EmployeesControllerEmployeeAddressRequest request);

    AddressDto toDto(AddressEntity entity);
}
