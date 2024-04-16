package dev.alexandreoliveira.microservices.cardsapi.configurations;

import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class OpenApiConfiguration {

    @Bean
    public OpenAPI openAPI() {
        var licence = new io.swagger.v3.oas.models.info.License()
                .name("Apache 2.0")
                .url("http://alexandreoliveira.dev");

        var contact = new io.swagger.v3.oas.models.info.Contact()
                .name("Alexandre Salvador de Oliveira")
                .email("contact@alexandreoliveira.dev")
                .url("http://alexandreoliveira.dev");

        var info = new io.swagger.v3.oas.models.info.Info()
                .title("Cards API microservice - Documentation")
                .description("AOsoluti API's")
                .version("v1 (0.0.1)")
                .contact(contact)
                .license(licence);

        var externalDocs = new io.swagger.v3.oas.models.ExternalDocumentation()
                .description("AOsoluti API's")
                .url("https://microservices-bank-cards-api.alexandreoliveira.dev/swagger-ui/index.html");

        return new OpenAPI()
                .info(info)
                .externalDocs(externalDocs);
    }
}
