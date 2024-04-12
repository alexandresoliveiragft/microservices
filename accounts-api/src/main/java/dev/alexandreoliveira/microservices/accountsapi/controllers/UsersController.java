package dev.alexandreoliveira.microservices.accountsapi.controllers;

import dev.alexandreoliveira.microservices.accountsapi.controllers.data.users.UserControllerCreateRequest;
import dev.alexandreoliveira.microservices.accountsapi.dtos.ResponseDTO;
import dev.alexandreoliveira.microservices.accountsapi.dtos.UserDTO;
import dev.alexandreoliveira.microservices.accountsapi.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@Tag(
        name = "Bank - Accounts Api - UserController",
        description = "This resource it's possible to execute action (POST, GET) for users."
)
@RestController
@RequestMapping("users")
@RequiredArgsConstructor
public class UsersController {

    private final UserService userService;

    @Operation(
            summary = "Create a new request."
    )
    @ApiResponse(
            responseCode = "201",
            description = "Details for created request.",
            headers = {
                    @Header(name = "location", description = "Link to recover a request for more details")
            }
    )
    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<ResponseDTO<UserDTO>> create(
            @Valid @RequestBody UserControllerCreateRequest request,
            UriComponentsBuilder uriComponentsBuilder) {
        UserDTO dto = userService.createUser(request);
        URI uri = uriComponentsBuilder.path("users/{id}").buildAndExpand(dto.getId()).toUri();
        return ResponseEntity.created(uri).body(new ResponseDTO<>(dto, HttpStatus.CREATED.value()));
    }

    @Operation(
            summary = "Show a user by identifier."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Details for created user and accounts if exists."
    )
    @GetMapping(value = "{id}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<ResponseDTO<UserDTO>> show(@PathVariable("id") UUID id) {
        UserDTO dto = userService.find(id);
        return ResponseEntity.ok(new ResponseDTO<>(dto, HttpStatus.OK.value()));
    }

}
