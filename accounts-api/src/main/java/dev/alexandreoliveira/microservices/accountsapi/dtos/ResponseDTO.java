package dev.alexandreoliveira.microservices.accountsapi.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.http.HttpStatus;

import java.util.Collections;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ResponseDTO<T>(
        T data,
        Integer status,
        String errorClass,
        List<String> errors
) {

    public ResponseDTO(T data, Integer status) {
        this(data, status, null, Collections.emptyList());
    }

    public ResponseDTO(Integer status, String errorClass, List<String> errors) {
        this(null, status, errorClass, errors);
    }
}
