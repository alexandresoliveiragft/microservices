package dev.alexandreoliveira.microservices.employeesapi.properties;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

@ConfigurationProperties(prefix = "api.cache")
@Getter
public class CacheProperties {

    private final Ttl ttl;

    @ConstructorBinding
    public CacheProperties(Ttl ttl) {
        this.ttl = ttl;
    }

    @Getter
    @RequiredArgsConstructor
    public static class Ttl {
        private final Integer employeesApiEmployee;
    }
}
