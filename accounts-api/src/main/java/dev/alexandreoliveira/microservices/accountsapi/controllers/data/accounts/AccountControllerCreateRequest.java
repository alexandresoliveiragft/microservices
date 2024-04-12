package dev.alexandreoliveira.microservices.accountsapi.controllers.data.accounts;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record AccountControllerCreateRequest(
        @NotNull UUID userId,
        @NotNull @Pattern(regexp = "PF|PJ") @Size(min = 2, max = 2) String accountType
) {
}
