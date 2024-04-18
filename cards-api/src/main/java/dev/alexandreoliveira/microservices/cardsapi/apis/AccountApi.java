package dev.alexandreoliveira.microservices.cardsapi.apis;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

import java.util.UUID;

@HttpExchange
public interface AccountApi {

    @GetExchange(value = "/users/{userId}/verify")
    void verifyUser(@PathVariable("userId") UUID userId);
}
