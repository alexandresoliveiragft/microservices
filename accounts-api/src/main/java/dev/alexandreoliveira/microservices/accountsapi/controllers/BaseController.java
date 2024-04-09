package dev.alexandreoliveira.microservices.accountsapi.controllers;

import dev.alexandreoliveira.microservices.accountsapi.dtos.ResponseDTO;
import dev.alexandreoliveira.microservices.accountsapi.services.exceptions.ServiceException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
public class BaseController {

    @Operation(
            summary = "Error to validate request data."
    )
    @ApiResponse(
            responseCode = "406",
            description = "This error occurs when request data is invalid."
    )
    @ResponseBody
    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseDTO<?>> handle(MethodArgumentNotValidException e) {
        List<String> errors = e.getFieldErrors()
                .stream()
                .map(f ->
                        String.format("(%s): %s. | Rejected Value: %s", f.getField(), f.getDefaultMessage(), f.getRejectedValue())
                )
                .toList();
        return ResponseEntity
                .status(HttpStatus.NOT_ACCEPTABLE)
                .body(new ResponseDTO<>(
                        HttpStatus.NOT_ACCEPTABLE.value(),
                        e.getClass().getName(),
                        errors));
    }

    @Operation(
            summary = "Internal error."
    )
    @ApiResponse(
            responseCode = "400",
            description = "This error occurs when business logic isn't ok."
    )
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ServiceException.class)
    public ResponseEntity<ResponseDTO<?>> handle(ServiceException e) {
        List<String> errors = List.of(e.getMessage());
        return ResponseEntity
                .badRequest()
                .body(new ResponseDTO<>(
                        HttpStatus.BAD_REQUEST.value(),
                        e.getClass().getName(),
                        errors));
    }
}
