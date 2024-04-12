package dev.alexandreoliveira.microservices.accountsapi.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class UserDto extends BaseDto {

    @NotNull
    @NotEmpty
    @Size(min = 3, max = 100)
    private String name;

    @NotNull
    @NotEmpty
    @Size(min = 3, max = 100)
    @Email
    private String email;

    @NotNull
    @NotEmpty
    @Size(min = 3, max = 100)
    private String mobileNumber;

    private List<AccountDto> accounts;
}
