package dev.alexandreoliveira.microservices.cardsapi.controllers.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.util.UUID;

@Schema
public record CardsControllerCreateRequest(
        @NotNull UUID externalId,

        @NotEmpty
        @Pattern(regexp = "CREDT|DEBIT|DBCRE") String cardType,

        @NotNull Byte dueDay
) {}
