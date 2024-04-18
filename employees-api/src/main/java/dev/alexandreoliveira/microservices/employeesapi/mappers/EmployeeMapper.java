package dev.alexandreoliveira.microservices.employeesapi.mappers;

import dev.alexandreoliveira.microservices.employeesapi.controllers.request.employees.EmployeesControllerCreateRequest;
import dev.alexandreoliveira.microservices.employeesapi.controllers.request.employees.EmployeesControllerIndexRequest;
import dev.alexandreoliveira.microservices.employeesapi.databases.entities.EmployeeEntity;
import dev.alexandreoliveira.microservices.employeesapi.databases.entities.enums.EmployeeJobLevelEnum;
import dev.alexandreoliveira.microservices.employeesapi.databases.entities.enums.EmployeeJobTitleEnum;
import dev.alexandreoliveira.microservices.employeesapi.dtos.EmployeeDto;
import dev.alexandreoliveira.microservices.employeesapi.exceptions.MapperException;
import org.mapstruct.EnumMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;

@Mapper(
        componentModel = "spring",
        uses = { AddressMapper.class },
        unexpectedValueMappingException = MapperException.class
)
public interface EmployeeMapper {

    @Mapping(source = "jobTitle", target = "jobTitle", qualifiedByName = "toJobTitle")
    @Mapping(source = "jobLevel", target = "jobLevel", qualifiedByName = "toJobLevel")
    EmployeeEntity toEntity(EmployeesControllerCreateRequest request);

    @Mapping(source = "jobTitle", target = "jobTitle", qualifiedByName = "toJobTitle")
    @Mapping(source = "jobLevel", target = "jobLevel", qualifiedByName = "toJobLevel")
    EmployeeEntity toEntity(EmployeesControllerIndexRequest request);

    @EnumMapping(nameTransformationStrategy = MappingConstants.CASE_TRANSFORMATION, configuration = "upper")
    @Named("toJobTitle")
    EmployeeJobTitleEnum toJobTitle(String jobTitle);

    @EnumMapping(nameTransformationStrategy = MappingConstants.CASE_TRANSFORMATION, configuration = "upper")
    @Named("toJobLevel")
    EmployeeJobLevelEnum toJobLevel(String jobLevel);

    EmployeeDto toDto(EmployeeEntity entity);
}
