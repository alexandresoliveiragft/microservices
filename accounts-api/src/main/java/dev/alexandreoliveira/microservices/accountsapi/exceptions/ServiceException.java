package dev.alexandreoliveira.microservices.accountsapi.exceptions;

public class ServiceException extends RuntimeException {

    public ServiceException(String message) {
        super(message);
    }
}
