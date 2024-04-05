package dev.alexandreoliveira.microservices.accountsapi.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AccountDTO {

    private Long id;

    @NotNull
    private Long userId;

    private String accountNumber;

    @NotNull
    @Pattern(regexp = "PF|PJ")
    @Size(min = 2, max = 2)
    private String accountType;
}
