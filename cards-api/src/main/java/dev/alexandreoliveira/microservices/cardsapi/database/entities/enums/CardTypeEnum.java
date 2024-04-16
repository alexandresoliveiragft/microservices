package dev.alexandreoliveira.microservices.cardsapi.database.entities.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum CardTypeEnum {
    DEBIT("Debit"),
    CREDT("Credit"),
    DBCRE("Debit and Credit");

    private final String description;
}
