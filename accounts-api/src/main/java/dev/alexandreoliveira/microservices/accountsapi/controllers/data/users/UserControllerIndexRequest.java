package dev.alexandreoliveira.microservices.accountsapi.controllers.data.users;

import java.time.LocalDateTime;

public record UserControllerIndexRequest(
        String name,
        String email,
        String mobileNumber,
        LocalDateTime createdAtStart,
        LocalDateTime createdAtEnd,
        String createdBy,
        LocalDateTime updatedAtStart,
        LocalDateTime updatedAtEnd,
        String updatedBy
) {}
