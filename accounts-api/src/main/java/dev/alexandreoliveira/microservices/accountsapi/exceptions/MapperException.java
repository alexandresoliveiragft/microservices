package dev.alexandreoliveira.microservices.accountsapi.exceptions;

public class MapperException extends RuntimeException {

    public MapperException(String message) {
        super(message);
    }

    public MapperException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
