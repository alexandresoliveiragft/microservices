package dev.alexandreoliveira.microservices.employeesapi.dtos;

import java.util.Collections;
import java.util.List;

public record ResponseDto<T>(
        T data,
        String errorClass,
        List<String> errors
) {

    public ResponseDto(T data) {
        this(data, null, Collections.emptyList());
    }

    public ResponseDto(String errorClass, List<String> errors) {
        this(null, errorClass, errors);
    }
}
