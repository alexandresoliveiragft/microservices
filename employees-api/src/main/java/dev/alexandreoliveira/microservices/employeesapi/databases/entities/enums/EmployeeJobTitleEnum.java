package dev.alexandreoliveira.microservices.employeesapi.databases.entities.enums;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum EmployeeJobTitleEnum {
    BANKER,
    CREDIT_ANALYST,
    LOAN,
    ANALYST,
    CARD_ANALYST;

    public static final EmployeeJobTitleEnum[] VALUES = values();
    public static final String REGEX = Stream.of(VALUES).map(Enum::name).collect(Collectors.joining("|"));
}
