package dev.alexandreoliveira.microservices.accountsapi.controllers;

import dev.alexandreoliveira.microservices.accountsapi.controllers.data.accounts.AccountControllerCreateRequest;
import dev.alexandreoliveira.microservices.accountsapi.dtos.AccountDto;
import dev.alexandreoliveira.microservices.accountsapi.dtos.ResponseDto;
import dev.alexandreoliveira.microservices.accountsapi.services.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Tag(
        name = "Bank - Accounts Api - AccountsController",
        description = "This resource it's possible to execute action (POST) for accounts."
)
@RestController
@RequestMapping("accounts")
@RequiredArgsConstructor
public class AccountsController {

    private final AccountService accountService;

    @Operation(
            summary = "Create a new data."
    )
    @ApiResponse(
            responseCode = "201",
            description = "Details for create data.",
            headers = {
                    @Header(name = "location", description = "Link to recover a data for more details")
            }
    )
    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<ResponseDto<AccountDto>> create(
            @Valid @RequestBody AccountControllerCreateRequest request,
            UriComponentsBuilder uriComponentsBuilder
    ) {
        AccountDto savedAccount = accountService.createAccount(request);
        URI uri = uriComponentsBuilder
                .path("accounts/{accountNumber}")
                .buildAndExpand(savedAccount.getAccountNumber())
                .toUri();
        return ResponseEntity.created(uri).body(new ResponseDto<>(savedAccount));
    }
}
