package dev.alexandreoliveira.microservices.accountsapi.services.exceptions;

public class ServiceException extends RuntimeException {

    public ServiceException(String message) {
        super(message);
    }
}
