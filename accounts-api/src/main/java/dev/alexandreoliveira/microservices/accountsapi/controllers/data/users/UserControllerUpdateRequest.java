package dev.alexandreoliveira.microservices.accountsapi.controllers.data.users;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record UserControllerUpdateRequest(
        @NotNull UUID id,
        String name,
        String email,
        String mobileNumber
) {}
