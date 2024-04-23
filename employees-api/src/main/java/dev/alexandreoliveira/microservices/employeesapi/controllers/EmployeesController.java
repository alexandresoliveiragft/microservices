package dev.alexandreoliveira.microservices.employeesapi.controllers;

import dev.alexandreoliveira.microservices.employeesapi.controllers.request.employees.EmployeesControllerCreateRequest;
import dev.alexandreoliveira.microservices.employeesapi.controllers.request.employees.EmployeesControllerIndexRequest;
import dev.alexandreoliveira.microservices.employeesapi.dtos.EmployeeDto;
import dev.alexandreoliveira.microservices.employeesapi.dtos.EmployeeDtoRepresentationModelAssembler;
import dev.alexandreoliveira.microservices.employeesapi.dtos.ResponseDto;
import dev.alexandreoliveira.microservices.employeesapi.services.EmployeesService;
import io.github.resilience4j.retry.annotation.Retry;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Tag(
        name = "Bank - Employees Api - EmployeesController",
        description = "This resource it's possible to execute action (POST, GET (Show/Index)) for cards."
)
@RestController
@RequestMapping("employees")
@RequiredArgsConstructor
@Slf4j
public class EmployeesController {

    private final EmployeesService employeesService;
    private final EmployeeDtoRepresentationModelAssembler employeeAssembler;

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
    public ResponseEntity<ResponseDto<EntityModel<EmployeeDto>>> create(
            @Valid @RequestBody EmployeesControllerCreateRequest request) {
        EmployeeDto dto = employeesService.create(request);
        EntityModel<EmployeeDto> model = employeeAssembler.toModel(dto);
        return ResponseEntity
                .created(model.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(new ResponseDto<>(model));
    }

    @Operation(summary = "Show a employees by identifier.")
    @ApiResponse(
            responseCode = "200",
            description = "Details for created employee, if exists."
    )
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "{id}", produces = {MediaType.APPLICATION_JSON_VALUE})
    @Retry(name = "show", fallbackMethod = "showFallback")
    public ResponseEntity<ResponseDto<EntityModel<EmployeeDto>>> show(@PathVariable("id") UUID id) {
        EmployeeDto dto = employeesService.show(id);
        return ResponseEntity.ok(new ResponseDto<>(employeeAssembler.toModel(dto)));
    }

    public ResponseEntity<String> showFallback(Throwable throwable) {
        return ResponseEntity
                .status(HttpStatus.BAD_GATEWAY)
                .body(throwable.getMessage());
    }

    @Operation(summary = "Show all employees by params.")
    @ApiResponse(
            responseCode = "200",
            description = "All employees by params."
    )
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(produces = {MediaType.APPLICATION_JSON_VALUE})
    @Retry(name = "index", fallbackMethod = "indexFallback")
    public ResponseEntity<Page<EntityModel<EmployeeDto>>> index(
            EmployeesControllerIndexRequest request,
            @PageableDefault(size = 5)
            @SortDefault.SortDefaults({
                    @SortDefault(sort = "name", direction = Sort.Direction.ASC)
            }) Pageable pageable
    ) {
        Page<EmployeeDto> pageUsers = employeesService.index(request, pageable);
        Page<EntityModel<EmployeeDto>> userModels = pageUsers.map(employeeAssembler::toModel);
        return ResponseEntity.ok(userModels);
    }

    public ResponseEntity<String> indexFallback(Throwable throwable) {
        return ResponseEntity
                .status(HttpStatus.BAD_GATEWAY)
                .body(throwable.getMessage());
    }
}
