package dev.alexandreoliveira.microservices.gatewayapi.components;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Order(1)
@Component
public class RequestTraceFilterComponent implements GlobalFilter {

    private static final Logger logger = LoggerFactory.getLogger(RequestTraceFilterComponent.class);

    private final FilterComponent filterComponent;

    public RequestTraceFilterComponent(FilterComponent filterComponent) {
        this.filterComponent = filterComponent;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        HttpHeaders requestHeaders = exchange.getRequest().getHeaders();
        if (isCorrelationIdPresent(requestHeaders)) {
            logger.debug("bank-correlation-id found in RequestTraceFilterComponent : {}",
                    filterComponent.getCorrelationId(requestHeaders));
        } else {
            String correlationID = generateCorrelationId();
            exchange = filterComponent.setCorrelationId(exchange, correlationID);
            logger.debug("bank-correlation-id generated in RequestTraceFilterComponent : {}", correlationID);
        }
        return chain.filter(exchange);
    }

    private boolean isCorrelationIdPresent(HttpHeaders requestHeaders) {
        if (filterComponent.getCorrelationId(requestHeaders) != null) {
            return true;
        } else {
            return false;
        }
    }

    private String generateCorrelationId() {
        return java.util.UUID.randomUUID().toString();
    }
}
