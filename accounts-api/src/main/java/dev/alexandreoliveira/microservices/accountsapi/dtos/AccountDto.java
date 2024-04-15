package dev.alexandreoliveira.microservices.accountsapi.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
public class AccountDto extends BaseDto implements Serializable {

    @NotNull
    private UUID userId;

    private String accountNumber;

    @NotNull
    @Pattern(regexp = "PF|PJ")
    @Size(min = 2, max = 2)
    private String accountType;
}
