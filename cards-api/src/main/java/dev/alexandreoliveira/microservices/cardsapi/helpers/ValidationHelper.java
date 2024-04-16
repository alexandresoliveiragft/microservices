package dev.alexandreoliveira.microservices.cardsapi.helpers;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.springframework.util.ReflectionUtils;

import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;

public class ValidationHelper {

    public static <T> void isValid(T model, Class<?> ...groups) {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            Validator validator = factory.getValidator();
            Set<ConstraintViolation<T>> violations = validator.validate(model, groups);
            if (!violations.isEmpty()) {
                throw new ConstraintViolationException(violations);
            }
        } catch (Exception e) {
            throw e;
        }
    }

    public static <T> boolean isAllNull(T model) {
        return Stream
                .of(model.getClass().getDeclaredFields())
                .map(field -> ReflectionUtils.findMethod(model.getClass(), field.getName()))
                .filter(Objects::nonNull)
                .map(method -> ReflectionUtils.invokeMethod(method, model))
                .allMatch(Objects::isNull);
    }
}
