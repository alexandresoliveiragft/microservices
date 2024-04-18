package dev.alexandreoliveira.microservices.cardsapi.exceptions;

public class ServiceException extends RuntimeException {

    public ServiceException(String message) {
        super(message);
    }
}
