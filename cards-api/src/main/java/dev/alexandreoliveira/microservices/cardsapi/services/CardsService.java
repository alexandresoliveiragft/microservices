package dev.alexandreoliveira.microservices.cardsapi.services;

import dev.alexandreoliveira.microservices.cardsapi.apis.AccountApi;
import dev.alexandreoliveira.microservices.cardsapi.controllers.requests.CardsControllerCreateRequest;
import dev.alexandreoliveira.microservices.cardsapi.controllers.requests.CardsControllerUpdateDueDayRequest;
import dev.alexandreoliveira.microservices.cardsapi.database.entities.CardEntity;
import dev.alexandreoliveira.microservices.cardsapi.database.repositories.CardsRepository;
import dev.alexandreoliveira.microservices.cardsapi.dtos.CardDto;
import dev.alexandreoliveira.microservices.cardsapi.helpers.CreditCardNumbersHelper;
import dev.alexandreoliveira.microservices.cardsapi.mappers.CardMapper;
import dev.alexandreoliveira.microservices.cardsapi.exceptions.ServiceException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Random;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CardsService {

    private final CardsRepository cardsRepository;
    private final CardMapper cardMapper;
    private final AccountApi accountApi;

    public CardDto create(CardsControllerCreateRequest request) {
        accountApi.verifyUser(request.externalId());

        long epochSecond = LocalDateTime.now().toEpochSecond(ZoneOffset.of("-0300"));
        int randomNumber = new Random().ints(1, Integer.MAX_VALUE).findFirst().getAsInt();
        String finalRandomCardNumber = String.join("", CreditCardNumbersHelper.masterCardNumber(), String.valueOf(epochSecond), String.valueOf(randomNumber));
        String cardNumber = String.format("%" + 16 + "s", finalRandomCardNumber).replace(' ', '0');

        int randomSecureNumber = new Random().ints(100, 999).findFirst().getAsInt();

        var cardEntity = new CardEntity();
        cardEntity.setExternalId(request.externalId());
        cardEntity.setCardNumber(cardNumber.substring(0, 16));
        cardEntity.setValidDate(LocalDateTime.now().plusYears(2));
        cardEntity.setCardType(cardMapper.toTransformCardType(request.cardType()));
        cardEntity.setSecureCode(String.valueOf(randomSecureNumber));
        cardEntity.setLimitValue(new BigDecimal("999.99"));

        CardEntity savedCard = cardsRepository.save(cardEntity);

        return cardMapper.toDto(savedCard);
    }

    public CardDto show(UUID id) {
        CardEntity cardEntity = cardsRepository
                .findById(id)
                .orElseThrow(() -> new SecurityException("Card not found!"));
        return cardMapper.toDto(cardEntity);
    }

    public CardDto updateDueDay(String cardNumber, CardsControllerUpdateDueDayRequest request) {
        if (!cardNumber.equals(request.cardNumber())) {
            throw new ServiceException("Operation not accept: You try to update a different card number.");
        }

        accountApi.verifyUser(request.externalId());

        ExampleMatcher exampleMatcher = ExampleMatcher
                .matchingAll()
                .withStringMatcher(ExampleMatcher.StringMatcher.EXACT)
                .withIgnoreNullValues()
                .withIgnoreCase(false);

        var exampleCardEntity = new CardEntity();
        exampleCardEntity.setCardNumber(request.cardNumber());
        exampleCardEntity.setExternalId(request.externalId());

        CardEntity cardEntity = cardsRepository
                .findOne(Example.of(exampleCardEntity, exampleMatcher))
                .orElseThrow(() -> new ServiceException("Card not found."));

        cardEntity.setDueDay(request.dueDay());

        cardsRepository.save(cardEntity);

        return cardMapper.toDto(cardEntity);
    }
}
