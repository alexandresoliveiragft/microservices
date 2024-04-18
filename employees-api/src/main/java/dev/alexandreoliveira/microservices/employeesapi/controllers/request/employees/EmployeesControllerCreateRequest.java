package dev.alexandreoliveira.microservices.employeesapi.controllers.request.employees;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record EmployeesControllerCreateRequest(
        @NotNull
        @NotEmpty
        @Size(min = 5, max = 100) String name,

        @NotNull
        @NotEmpty
        @Size(min = 5, max = 100)
        @Email String email,

        @NotNull
        @NotEmpty
        @Size(min = 8, max = 20) String phoneNumber,

        @NotNull
        @NotEmpty
        @Pattern(regexp = "BANKER|CREDIT_ANALYST|LOAN|ANALYST|CARD_ANALYST") String jobTitle,

        @NotNull
        @NotEmpty
        @Pattern(regexp = "INTERN|JUNIOR|MID|SENIOR") String jobLevel,

        @NotNull EmployeesControllerEmployeeAddressRequest address
) {

        public record EmployeesControllerEmployeeAddressRequest(
                @NotNull
                @NotEmpty
                @Size(min = 3, max = 150) String description,

                @NotNull
                @PositiveOrZero
                @Max(Integer.MAX_VALUE) Integer number,

                @Size(max = 30) String complement,

                @NotNull
                @NotEmpty
                @Size(min = 3, max = 8) String zipCode,

                @NotNull
                @NotEmpty
                @Size(min = 3, max = 50) String neighborhood,

                @NotNull
                @NotEmpty
                @Size(min = 3, max = 50) String city,

                @NotNull
                @NotEmpty
                @Size(min = 3, max = 50) String state
        ) {}
}
