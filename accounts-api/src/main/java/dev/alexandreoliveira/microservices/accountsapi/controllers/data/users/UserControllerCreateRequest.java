package dev.alexandreoliveira.microservices.accountsapi.controllers.data.users;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UserControllerCreateRequest(
        @NotNull
        @NotEmpty
        @Size(min = 3, max = 100) String name,

        @NotNull
        @NotEmpty
        @Size(min = 3, max = 100)
        @Email String email,

        @NotNull
        @NotEmpty
        @Size(min = 3, max = 100) String mobileNumber
) {}
