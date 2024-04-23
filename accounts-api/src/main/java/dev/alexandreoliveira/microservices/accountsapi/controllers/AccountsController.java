package dev.alexandreoliveira.microservices.accountsapi.controllers;

import dev.alexandreoliveira.microservices.accountsapi.controllers.data.accounts.AccountControllerCreateRequest;
import dev.alexandreoliveira.microservices.accountsapi.dtos.AccountDto;
import dev.alexandreoliveira.microservices.accountsapi.dtos.AccountDtoRepresentationModelAssembler;
import dev.alexandreoliveira.microservices.accountsapi.dtos.ResponseDto;
import dev.alexandreoliveira.microservices.accountsapi.services.AccountService;
import io.github.resilience4j.retry.annotation.Retry;
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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.UUID;

@Tag(
        name = "Bank - Accounts Api - AccountsController",
        description = "This resource it's possible to execute action (POST) for accounts."
)
@RestController
@RequestMapping("accounts")
@RequiredArgsConstructor
public class AccountsController {

    private final AccountService accountService;
    private final AccountDtoRepresentationModelAssembler assembler;

    @Operation(summary = "Create a new data.")
    @ApiResponse(
            responseCode = "201",
            description = "Details for create data.",
            headers = {
                    @Header(name = "location", description = "Link to get a data for more details")
            }
    )
    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<ResponseDto<EntityModel<AccountDto>>> create(
            @Valid @RequestBody AccountControllerCreateRequest request,
            UriComponentsBuilder uriComponentsBuilder
    ) {
        AccountDto savedAccount = accountService.create(request);
        EntityModel<AccountDto> model = assembler.toModel(savedAccount);
        return ResponseEntity
                .created(model.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(new ResponseDto<>(model));
    }

    @Operation(summary = "Show account by id")
    @ApiResponse(
            responseCode = "200",
            description = "Details for account"
    )
    @GetMapping(value = "{id}", produces = {MediaType.APPLICATION_JSON_VALUE})
    @Retry(name = "show", fallbackMethod = "showFallback")
    public ResponseEntity<EntityModel<AccountDto>> show(
            @PathVariable("id")UUID id
    ) {
        AccountDto account = accountService.show(id);
        return ResponseEntity.ok(assembler.toModel(account));
    }

    public ResponseEntity<String> showFallback(Throwable throwable) {
        return ResponseEntity
                .status(HttpStatus.BAD_GATEWAY)
                .body(throwable.getMessage());
    }

    @Operation(summary = "Delete account by id")
    @ApiResponse(
            responseCode = "204",
            description = "Delete account"
    )
    @DeleteMapping(value = "{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") UUID id) {
        accountService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
