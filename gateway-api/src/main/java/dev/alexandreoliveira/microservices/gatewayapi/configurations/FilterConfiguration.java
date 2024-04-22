package dev.alexandreoliveira.microservices.gatewayapi.configurations;

import dev.alexandreoliveira.microservices.gatewayapi.components.FilterComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import reactor.core.publisher.Mono;

@Configuration
public class FilterConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(FilterConfiguration.class);

    private final FilterComponent filterComponent;

    public FilterConfiguration(FilterComponent filterComponent) {
        this.filterComponent = filterComponent;
    }

    @Bean
    public GlobalFilter postGlobalFilter() {
        return (exchange, chain) -> {
            return chain.filter(exchange).then(Mono.fromRunnable(() -> {
                HttpHeaders requestHeaders = exchange.getRequest().getHeaders();
                String correlationId = filterComponent.getCorrelationId(requestHeaders);
                logger.debug("Updated the correlation id to the outbound headers: {}", correlationId);
                exchange.getResponse().getHeaders().add(FilterComponent.CORRELATION_ID, correlationId);
            }));
        };
    }
}
