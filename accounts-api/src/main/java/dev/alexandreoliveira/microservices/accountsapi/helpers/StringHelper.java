package dev.alexandreoliveira.microservices.accountsapi.helpers;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Objects;

@Component
public class StringHelper {

    public String requiredNonBlankOrElse(String obj, String orElse) {
        Objects.requireNonNull(orElse, "orElse attribute is required");
        return StringUtils.hasText(obj) ? obj : orElse;
    }
}
