package dev.alexandreoliveira.microservices.gatewayapi.configurations;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import org.springframework.cloud.circuitbreaker.resilience4j.ReactiveResilience4JCircuitBreakerFactory;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JConfigBuilder;
import org.springframework.cloud.client.circuitbreaker.Customizer;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import reactor.core.publisher.Mono;

import java.time.Duration;
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
                                                .retry(retryConfig -> retryConfig
                                                        .setRetries(3)
                                                        .setMethods(HttpMethod.GET)
                                                        .setBackoff(
                                                                Duration.ofMillis(100),
                                                                Duration.ofMillis(1000),
                                                                2,
                                                                true
                                                        )
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
                                                .retry(retryConfig -> retryConfig
                                                        .setRetries(3)
                                                        .setMethods(HttpMethod.GET)
                                                        .setBackoff(
                                                                Duration.ofMillis(100),
                                                                Duration.ofMillis(1000),
                                                                2,
                                                                true
                                                        )
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
                                                .retry(retryConfig -> retryConfig
                                                        .setRetries(3)
                                                        .setMethods(HttpMethod.GET)
                                                        .setBackoff(
                                                                Duration.ofMillis(100),
                                                                Duration.ofMillis(1000),
                                                                2,
                                                                true
                                                        )
                                                )
                                                .requestRateLimiter(config -> config
                                                        .setRateLimiter(redisRateLimiter())
                                                        .setKeyResolver(keyResolver())
                                                )
                                )
                                .uri("lb://EMPLOYEES-API")
                )
                .build();
    }

    @Bean
    public Customizer<ReactiveResilience4JCircuitBreakerFactory> reactiveResilience4JCircuitBreakerFactoryCustomizer() {
        return factory -> factory.configureDefault(id -> new Resilience4JConfigBuilder(id)
                .circuitBreakerConfig(CircuitBreakerConfig.ofDefaults())
                .timeLimiterConfig(TimeLimiterConfig
                        .custom()
                        .timeoutDuration(Duration.ofSeconds(4))
                        .build()
                )
                .build()
        );
    }

    @Bean
    public RedisRateLimiter redisRateLimiter() {
        return new RedisRateLimiter(1,1,1);
    }

    @Bean
    KeyResolver keyResolver() {
        return exchange -> Mono
                .justOrEmpty(exchange
                        .getRequest()
                        .getHeaders()
                        .getFirst("user")
                )
                .defaultIfEmpty("anonymous");
    }
}
