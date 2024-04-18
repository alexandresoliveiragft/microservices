package dev.alexandreoliveira.microservices.employeesapi.exceptions;

public class MapperException extends RuntimeException {

    public MapperException(String message) {
        super(message);
    }

    public MapperException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
