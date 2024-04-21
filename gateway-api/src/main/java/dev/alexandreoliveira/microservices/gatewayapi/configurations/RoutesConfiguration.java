package dev.alexandreoliveira.microservices.gatewayapi.configurations;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;

@Configuration
public class RoutesConfiguration {

    @Bean
    public RouteLocator bankRouteLocator(RouteLocatorBuilder routeLocatorBuilder) {
        return routeLocatorBuilder
                .routes()
                .route(predicateSpec ->
                        predicateSpec
                                .path("/bank/accounts/**")
                                .filters(gatewayFilterSpec ->
                                        gatewayFilterSpec
                                                .rewritePath("/bank/accounts/(?<segment>.*)", "/${segment}")
                                                .addResponseHeader("X-Response-Time", LocalDateTime.now().toString())
                                                .circuitBreaker(config -> config
                                                        .setName("circuitBreakerAccounts")
                                                        .setFallbackUri("forward:/fallbacks/accounts")
                                                )
                                )
                                .uri("lb://ACCOUNTS-API")
                )
                .route(predicateSpec ->
                        predicateSpec
                                .path("/bank/cards/**")
                                .filters(gatewayFilterSpec ->
                                        gatewayFilterSpec
                                                .rewritePath("/bank/cards/(?<segment>.*)", "/${segment}")
                                                .addResponseHeader("X-Response-Time", LocalDateTime.now().toString())
                                                .circuitBreaker(config -> config
                                                        .setName("circuitBreakerCards")
                                                        .setFallbackUri("forward:/fallbacks/cards")
                                                )
                                )
                                .uri("lb://CARDS-API")
                )
                .route(predicateSpec ->
                        predicateSpec
                                .path("/bank/employees/**")
                                .filters(gatewayFilterSpec ->
                                        gatewayFilterSpec
                                                .rewritePath("/bank/employees/(?<segment>.*)", "/${segment}")
                                                .addResponseHeader("X-Response-Time", LocalDateTime.now().toString())
                                                .circuitBreaker(config -> config
                                                        .setName("circuitBreakerEmployees")
                                                        .setFallbackUri("forward:/fallbacks/employees")
                                                )
                                )
                                .uri("lb://EMPLOYEES-API")
                )
                .build();
    }
}
