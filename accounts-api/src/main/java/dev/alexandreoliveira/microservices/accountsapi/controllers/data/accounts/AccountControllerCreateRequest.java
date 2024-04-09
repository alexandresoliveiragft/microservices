package dev.alexandreoliveira.microservices.accountsapi.controllers.data.accounts;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record AccountControllerCreateRequest(
        @NotNull Long userId,
        @NotNull @Pattern(regexp = "PF|PJ") @Size(min = 2, max = 2) String accountType
) {
}
