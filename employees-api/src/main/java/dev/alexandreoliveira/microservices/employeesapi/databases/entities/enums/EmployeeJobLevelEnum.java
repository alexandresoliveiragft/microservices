package dev.alexandreoliveira.microservices.employeesapi.databases.entities.enums;

import lombok.Getter;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum EmployeeJobLevelEnum {
    INTERN,
    JUNIOR,
    MID,
    SENIOR;

    public static final EmployeeJobLevelEnum[] VALUES = values();
    public static final String REGEX = Stream.of(VALUES).map(Enum::name).collect(Collectors.joining("|"));
}
