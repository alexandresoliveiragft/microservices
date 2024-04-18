package dev.alexandreoliveira.microservices.employeesapi.exceptions;

public class ServiceException extends RuntimeException {

    public ServiceException(String message) {
        super(message);
    }
}
