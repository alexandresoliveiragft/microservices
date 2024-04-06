package dev.alexandreoliveira.microservices.accountsapi.controllers;

import dev.alexandreoliveira.microservices.accountsapi.dtos.UserDTO;
import dev.alexandreoliveira.microservices.accountsapi.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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

@Tag(
        name = "Bank - Accounts Api - UserController",
        description = "This resource it's possible to execute action (POST, GET) for users."
)
@RestController
@RequestMapping("users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(
            summary = "Create a new user."
    )
    @ApiResponse(
            responseCode = "201",
            description = "Details for created user.",
            headers = {
                    @Header(name = "location", description = "Link to recover a user for more details")
            }
    )
    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<UserDTO> create(
            @Valid @RequestBody UserDTO user,
            UriComponentsBuilder uriComponentsBuilder) {
        UserDTO userSaved = userService.createUser(user);
        URI uri = uriComponentsBuilder.path("users/{id}").buildAndExpand(userSaved.getId()).toUri();
        return ResponseEntity.created(uri).body(userSaved);
    }

    @Operation(
            summary = "Show a user by identifier."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Details for created user and accounts if exists."
    )
    @GetMapping(value = "{id}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<UserDTO> show(@PathVariable("id") Long id) {
        return ResponseEntity.ok(userService.find(id));
    }

}
