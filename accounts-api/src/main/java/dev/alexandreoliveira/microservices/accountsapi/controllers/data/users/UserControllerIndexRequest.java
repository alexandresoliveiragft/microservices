package dev.alexandreoliveira.microservices.accountsapi.controllers.data.users;

import java.time.LocalDateTime;
import java.util.List;

public record UserControllerIndexRequest(
        String name,
        String email,
        String mobileNumber,
        List<String> accountsNumber,
        LocalDateTime createdAtStart,
        LocalDateTime createdAtEnd,
        String createdBy,
        LocalDateTime updatedAtStart,
        LocalDateTime updatedAtEnd,
        String updatedBy
) {}
