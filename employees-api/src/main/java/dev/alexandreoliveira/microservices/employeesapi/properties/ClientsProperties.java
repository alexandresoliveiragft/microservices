package dev.alexandreoliveira.microservices.employeesapi.properties;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

@ConfigurationProperties(prefix = "api.clients")
@Getter
public class ClientsProperties {

    private final String accountApi;

    @ConstructorBinding
    public ClientsProperties(
            String accountApi
    ) {
        this.accountApi = accountApi;
    }
}
