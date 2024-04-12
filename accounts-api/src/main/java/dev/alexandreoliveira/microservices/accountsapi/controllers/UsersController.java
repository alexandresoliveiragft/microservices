package dev.alexandreoliveira.microservices.accountsapi.controllers;

import dev.alexandreoliveira.microservices.accountsapi.controllers.data.users.UserControllerCreateRequest;
import dev.alexandreoliveira.microservices.accountsapi.controllers.data.users.UserControllerIndexRequest;
import dev.alexandreoliveira.microservices.accountsapi.controllers.data.users.UserControllerUpdateRequest;
import dev.alexandreoliveira.microservices.accountsapi.dtos.ResponseDto;
import dev.alexandreoliveira.microservices.accountsapi.dtos.UserDto;
import dev.alexandreoliveira.microservices.accountsapi.dtos.UserDtoRepresentationModelAssembler;
import dev.alexandreoliveira.microservices.accountsapi.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
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
    private final UserDtoRepresentationModelAssembler assembler;

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
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<ResponseDto<EntityModel<UserDto>>> create(
            @Valid @RequestBody UserControllerCreateRequest request) {
        UserDto dto = userService.createUser(request);
        EntityModel<UserDto> model = assembler.toModel(dto);
        return ResponseEntity
                .created(model.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(new ResponseDto<>(model));
    }

    @Operation(
            summary = "Show a user by identifier."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Details for created user and accounts if exists."
    )
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "{id}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<ResponseDto<UserDto>> show(@PathVariable("id") UUID id) {
        UserDto dto = userService.find(id);
        return ResponseEntity.ok(new ResponseDto<>(dto));
    }

    @Operation(
            summary = "Show all users by params."
    )
    @ApiResponse(
            responseCode = "200",
            description = "All users by params."
    )
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Page<EntityModel<UserDto>>> index(
            @Valid UserControllerIndexRequest request,
            @PageableDefault(size = 5)
            @SortDefault.SortDefaults({
                    @SortDefault(sort = "name", direction = Sort.Direction.ASC)
            }) Pageable pageable
    ) {
        Page<UserDto> pageUsers = userService.index(request, pageable);
        Page<EntityModel<UserDto>> pageEntityModelUsers = pageUsers.map(assembler::toModel);
        return ResponseEntity.ok(pageEntityModelUsers);
    }

    @Operation(
            summary = "Update user by identifier."
    )
    @ApiResponse(
            responseCode = "200",
            description = "User updated."
    )
    @ResponseStatus(HttpStatus.OK)
    @PutMapping(value = "{id}", consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<ResponseDto<UserDto>> update(
            @Valid @RequestBody UserControllerUpdateRequest request
    ) {
        UserDto user = userService.update(request);
        return ResponseEntity.ok(new ResponseDto<>(user));
    }
}
