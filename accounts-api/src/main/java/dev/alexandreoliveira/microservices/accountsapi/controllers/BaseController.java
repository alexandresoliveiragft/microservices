package dev.alexandreoliveira.microservices.accountsapi.controllers;

import dev.alexandreoliveira.microservices.accountsapi.services.exceptions.ServiceException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
public class BaseController {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<List<String>> handle(MethodArgumentNotValidException e) {
        List<String> errors = e.getFieldErrors()
                .stream()
                .map(f ->
                        String.format("(%s): %s. | Rejected Value: %s", f.getField(), f.getDefaultMessage(), f.getRejectedValue())
                )
                .toList();
        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(ServiceException.class)
    public ResponseEntity<List<String>> handle(ServiceException e) {
        List<String> errors = List.of(e.getMessage());
        return ResponseEntity.badRequest().body(errors);
    }

}
