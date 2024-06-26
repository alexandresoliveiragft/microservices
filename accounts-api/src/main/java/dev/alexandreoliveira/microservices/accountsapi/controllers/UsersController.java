package dev.alexandreoliveira.microservices.accountsapi.controllers;

import dev.alexandreoliveira.microservices.accountsapi.controllers.data.users.UserControllerCreateRequest;
import dev.alexandreoliveira.microservices.accountsapi.controllers.data.users.UserControllerIndexRequest;
import dev.alexandreoliveira.microservices.accountsapi.controllers.data.users.UserControllerUpdateRequest;
import dev.alexandreoliveira.microservices.accountsapi.dtos.AccountDto;
import dev.alexandreoliveira.microservices.accountsapi.dtos.AccountDtoRepresentationModelAssembler;
import dev.alexandreoliveira.microservices.accountsapi.dtos.ResponseDto;
import dev.alexandreoliveira.microservices.accountsapi.dtos.UserDto;
import dev.alexandreoliveira.microservices.accountsapi.dtos.UserDtoRepresentationModelAssembler;
import dev.alexandreoliveira.microservices.accountsapi.services.UserService;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
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
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Tag(
        name = "Bank - Accounts Api - UserController",
        description = "This resource it's possible to execute action (POST, GET (Show/Index), PUT) for users."
)
@RestController
@RequestMapping("users")
@RequiredArgsConstructor
public class UsersController {

    private final UserService userService;
    private final UserDtoRepresentationModelAssembler assembler;
    private final AccountDtoRepresentationModelAssembler accountAssembler;

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
    public ResponseEntity<ResponseDto<EntityModel<UserDto>>> create(
            @Valid @RequestBody UserControllerCreateRequest request) {
        UserDto dto = userService.create(request);
        EntityModel<UserDto> model = assembler.toModel(dto);
        return ResponseEntity
                .created(model.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(new ResponseDto<>(model));
    }

    @Operation(summary = "Show a user by identifier.")
    @ApiResponse(
            responseCode = "200",
            description = "Details for created user and accounts if exists."
    )
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "{id}", produces = {MediaType.APPLICATION_JSON_VALUE})
    @Retry(name = "show", fallbackMethod = "showFallback")
    @RateLimiter(name= "index", fallbackMethod = "showFallback")
    public ResponseEntity<ResponseDto<EntityModel<UserDto>>> show(@PathVariable("id") UUID id) {
        UserDto dto = userService.show(id);
        return ResponseEntity.ok(new ResponseDto<>(assembler.toModel(dto)));
    }

    public ResponseEntity<ResponseDto<EntityModel<UserDto>>> showFallback(Throwable throwable) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .header("X-Fallback-Method", "UsersController.showFallback")
                .header("X-Fallback-Exception", throwable.getClass().getName())
                .header("X-Fallback-Exception-Message", throwable.getMessage())
                .body(new ResponseDto<>(null));
    }

    @Operation(summary = "Show all users by params.")
    @ApiResponse(
            responseCode = "200",
            description = "All users by params."
    )
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(produces = {MediaType.APPLICATION_JSON_VALUE})
    @Retry(name = "index", fallbackMethod = "indexFallback")
    @RateLimiter(name= "index", fallbackMethod = "indexFallback")
    public ResponseEntity<Page<EntityModel<UserDto>>> index(
            UserControllerIndexRequest request,
            @RequestParam(value = "isComplete", required = false, defaultValue = "false") Boolean isComplete,
            @PageableDefault(size = 5)
            @SortDefault.SortDefaults({
                    @SortDefault(sort = "name", direction = Sort.Direction.ASC)
            }) Pageable pageable
    ) {
        Page<UserDto> pageUsers = userService.index(request, isComplete, pageable);
        Page<EntityModel<UserDto>> userModels = pageUsers.map(assembler::toModel);

        if (isComplete) {
            userModels
                    .stream()
                    .filter(f -> Objects.nonNull(f.getContent()))
                    .filter(f -> !CollectionUtils.isEmpty(f.getContent().getAccounts()))
                    .forEach(model -> {
                        CollectionModel<EntityModel<AccountDto>> accountsModel = accountAssembler
                                .toCollectionModel(model.getContent().getAccounts());
                        model.getContent().setAccounts(null);
                        model.getContent().setAccountsComplete(new ArrayList<>());
                        model.getContent().getAccountsComplete().addAll(accountsModel.getContent());
                    });
        }

        return ResponseEntity.ok(userModels);
    }

    public ResponseEntity<Page<EntityModel<UserDto>>> indexFallback(Throwable throwable) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .header("X-Fallback-Method", "UsersController.indexFallback")
                .header("X-Fallback-Exception", throwable.getClass().getName())
                .header("X-Fallback-Exception-Message", throwable.getMessage())
                .body(Page.empty());
    }

    @Operation(summary = "Update user by identifier.")
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

    @Operation(summary = "Verify user by identifier.")
    @ApiResponse(
            responseCode = "200",
            description = "Check if user exists and active."
    )
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "{id}/verify")
    @Retry(name = "verify", fallbackMethod = "verifyFallback")
    @RateLimiter(name= "verify", fallbackMethod = "verifyFallback")
    public ResponseEntity<ResponseDto<Map<String, Boolean>>> verify(@PathVariable("id") UUID id) {
        userService.verify(id);
        Map<String, Boolean> data = Map.of("isValid", Boolean.TRUE);
        return ResponseEntity
                .ok(new ResponseDto<>(data));
    }

    public ResponseEntity<ResponseDto<Map<String, Boolean>>> verifyFallback(Throwable throwable) {
        Map<String, Boolean> data = Map.of("isValid", Boolean.FALSE);
        return ResponseEntity
                .status(HttpStatus.OK)
                .header("X-Fallback-Method", "UsersController.verifyFallback")
                .header("X-Fallback-Exception", throwable.getClass().getName())
                .header("X-Fallback-Exception-Message", throwable.getMessage())
                .body(new ResponseDto<>(data));
    }

    @Operation(summary = "Delete user by identifier.")
    @ApiResponse(
            responseCode = "204",
            description = "If user exists, desactivate then."
    )
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping(value = "{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") UUID id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
