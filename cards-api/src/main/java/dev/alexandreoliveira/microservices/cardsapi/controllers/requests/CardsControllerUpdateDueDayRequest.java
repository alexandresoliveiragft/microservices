package dev.alexandreoliveira.microservices.cardsapi.controllers.requests;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record CardsControllerUpdateDueDayRequest(
        @NotEmpty
        @Size(min = 16, max = 16) String cardNumber,

        @NotNull UUID externalId,

        @NotNull
        @Min(1)
        @Max(30) Byte dueDay
) {}
