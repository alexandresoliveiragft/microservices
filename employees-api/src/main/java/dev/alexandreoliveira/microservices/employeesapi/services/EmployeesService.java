package dev.alexandreoliveira.microservices.employeesapi.services;

import dev.alexandreoliveira.microservices.employeesapi.controllers.request.employees.EmployeesControllerCreateRequest;
import dev.alexandreoliveira.microservices.employeesapi.controllers.request.employees.EmployeesControllerIndexRequest;
import dev.alexandreoliveira.microservices.employeesapi.databases.entities.EmployeeEntity;
import dev.alexandreoliveira.microservices.employeesapi.databases.repositories.EmployeeRepository;
import dev.alexandreoliveira.microservices.employeesapi.dtos.AddressDto;
import dev.alexandreoliveira.microservices.employeesapi.dtos.EmployeeDto;
import dev.alexandreoliveira.microservices.employeesapi.exceptions.ServiceException;
import dev.alexandreoliveira.microservices.employeesapi.helpers.ValidationHelper;
import dev.alexandreoliveira.microservices.employeesapi.mappers.EmployeeMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EmployeesService {

    private final EmployeeRepository employeeRepository;
    private final EmployeeMapper employeeMapper;
    private final AddressesService addressesService;

    @Transactional(rollbackFor = {Throwable.class})
    public EmployeeDto create(EmployeesControllerCreateRequest request) {
        Optional<EmployeeEntity> optionalEmployee = employeeRepository.findByNameIgnoreCaseOrEmailIgnoreCaseOrPhoneNumber(
                request.name(),
                request.email(),
                request.phoneNumber()
        );

        if (optionalEmployee.isPresent()) {
            throw new ServiceException("Employee has registred");
        }

        EmployeeEntity employee = employeeMapper.toEntity(request);

        EmployeeEntity savedEmployee = employeeRepository.save(employee);

        EmployeeDto employeeDto = employeeMapper.toDto(savedEmployee);

        AddressDto addressDto = addressesService.create(savedEmployee.getId(), savedEmployee.getVersion(), request.address());
        employeeDto.setAddress(addressDto);

        return employeeDto;
    }

    public EmployeeDto show(UUID id) {
        EmployeeEntity cardEntity = employeeRepository
                .findById(id)
                .orElseThrow(() -> new SecurityException("Employee not found!"));
        return employeeMapper.toDto(cardEntity);
    }

    public Page<EmployeeDto> index(EmployeesControllerIndexRequest request, Pageable pageable) {
        if (ValidationHelper.isAllNull(request)) {
            throw new ServiceException("All parameters are null, please add some parameter to validate your search.");
        }

        ExampleMatcher exampleMatcher = ExampleMatcher
                .matchingAll()
                .withIgnoreNullValues()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
                .withIgnoreCase(true);

        EmployeeEntity exampleUserEntity = Objects.requireNonNullElse(
                employeeMapper.toEntity(request),
                new EmployeeEntity());

        Example<EmployeeEntity> entityExample = Example.of(exampleUserEntity, exampleMatcher);

        Specification<EmployeeEntity> where = employeeRepository.where(request, entityExample);

        Page<EmployeeEntity> users = employeeRepository.findAll(where, pageable);

        return users.map(employeeMapper::toDto);
    }
}
