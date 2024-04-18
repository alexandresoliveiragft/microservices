package dev.alexandreoliveira.microservices.cardsapi.configurations;

import dev.alexandreoliveira.microservices.cardsapi.apis.AccountApi;
import dev.alexandreoliveira.microservices.cardsapi.properties.ClientsProperties;
import dev.alexandreoliveira.microservices.cardsapi.exceptions.ServiceException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

import java.nio.charset.StandardCharsets;

@Configuration
@RequiredArgsConstructor
public class RestClientConfiguration {

    private final ClientsProperties clientsProperties;

    @Bean
    HttpServiceProxyFactory httpServiceProxyFactory() {
        RestClient restClient = RestClient
                .builder()
                .baseUrl(clientsProperties.getAccountApi())
                .defaultStatusHandler(HttpStatusCode::isError, (request, response) -> {
                    throw new ServiceException(new String(response.getBody().readAllBytes(), StandardCharsets.UTF_8));
                })
                .build();
        RestClientAdapter adapter = RestClientAdapter.create(restClient);
        return HttpServiceProxyFactory.builderFor(adapter).build();
    }

    @Bean
    @DependsOn("httpServiceProxyFactory")
    public AccountApi accountApi(HttpServiceProxyFactory factory) {
        return factory.createClient(AccountApi.class);
    }
}
