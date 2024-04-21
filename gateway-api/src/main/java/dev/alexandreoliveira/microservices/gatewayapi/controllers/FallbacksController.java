package dev.alexandreoliveira.microservices.gatewayapi.controllers;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("fallbacks")
public class FallbacksController {

    @GetMapping(value = "accounts", produces = {MediaType.TEXT_PLAIN_VALUE})
    public ResponseEntity<Mono<String>> fallbackAccounts() {
        return ResponseEntity
                .ok(Mono.just("Contact accounts-api support team for more information."));
    }

    @GetMapping(value = "cards", produces = {MediaType.TEXT_PLAIN_VALUE})
    public ResponseEntity<Mono<String>> fallbackCards() {
        return ResponseEntity
                .ok(Mono.just("Contact cards-api support team for more information."));
    }

    @GetMapping(value = "employees", produces = {MediaType.TEXT_PLAIN_VALUE})
    public ResponseEntity<Mono<String>> fallbackEmployees() {
        return ResponseEntity
                .ok(Mono.just("Contact employees-api support team for more information."));
    }
}
