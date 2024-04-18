package dev.alexandreoliveira.microservices.cardsapi.controllers;

import dev.alexandreoliveira.microservices.cardsapi.controllers.requests.CardsControllerCreateRequest;
import dev.alexandreoliveira.microservices.cardsapi.controllers.requests.CardsControllerUpdateDueDayRequest;
import dev.alexandreoliveira.microservices.cardsapi.dtos.CardDto;
import dev.alexandreoliveira.microservices.cardsapi.dtos.CardDtoRepresentationModelAssembler;
import dev.alexandreoliveira.microservices.cardsapi.dtos.ResponseDto;
import dev.alexandreoliveira.microservices.cardsapi.services.CardsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Tag(
        name = "Bank - Cards Api - CardsController",
        description = "This resource it's possible to execute action (POST, GET (Show/Index)) for cards."
)
@RestController
@RequestMapping("cards")
@RequiredArgsConstructor
public class CardsController {

    private final CardsService cardsService;
    private final CardDtoRepresentationModelAssembler assembler;

    @Operation(summary = "Create a new request.")
    @ApiResponse(
            responseCode = "201",
            description = "Details for created request.",
            headers = {
                    @Header(name = "location", description = "Link to get a request for more details")
            }
    )
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<ResponseDto<EntityModel<CardDto>>> create(
            @Valid @RequestBody CardsControllerCreateRequest request) {
        CardDto dto = cardsService.create(request);
        EntityModel<CardDto> model = assembler.toModel(dto);
        return ResponseEntity
                .created(model.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(new ResponseDto<>(model));
    }

    @Operation(summary = "Show a user by identifier.")
    @ApiResponse(
            responseCode = "200",
            description = "Details for created card if exists."
    )
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "{id}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<ResponseDto<EntityModel<CardDto>>> show(@PathVariable("id") UUID id) {
        CardDto dto = cardsService.show(id);
        return ResponseEntity.ok(new ResponseDto<>(assembler.toModel(dto)));
    }

    @Operation(summary = "Update due day by card number and user.")
    @ApiResponse(
            responseCode = "200",
            description = "Details for created card if exists."
    )
    @ResponseStatus(HttpStatus.OK)
    @PatchMapping(value = "{cardNumber}", consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<ResponseDto<EntityModel<CardDto>>> updateDueDay(
            @PathVariable("cardNumber") String cardNumber,
            @Valid @RequestBody CardsControllerUpdateDueDayRequest request) {
        CardDto dto = cardsService.updateDueDay(cardNumber, request);
        return ResponseEntity.ok(new ResponseDto<>(assembler.toModel(dto)));
    }
}
