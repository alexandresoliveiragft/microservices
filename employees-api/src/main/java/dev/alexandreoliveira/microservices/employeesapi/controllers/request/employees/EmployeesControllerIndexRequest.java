package dev.alexandreoliveira.microservices.employeesapi.controllers.request.employees;

public record EmployeesControllerIndexRequest(
        String name,
        String email,
        String phoneNumber,
        String jobTitle,
        String jobLevel,
        Boolean isEnabled
) {}
