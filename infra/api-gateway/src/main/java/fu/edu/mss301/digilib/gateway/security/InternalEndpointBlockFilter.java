package fu.edu.mss301.digilib.gateway.security;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.util.pattern.PathPattern;
import org.springframework.web.util.pattern.PathPatternParser;
import reactor.core.publisher.Mono;

@Component
public class InternalEndpointBlockFilter implements GlobalFilter, Ordered {

    private static final PathPattern INTERNAL_MEMBER_PATH =
            new PathPatternParser().parse("/api/v1/members/internal/**");

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        if (!INTERNAL_MEMBER_PATH.matches(exchange.getRequest().getPath().pathWithinApplication())) {
            return chain.filter(exchange);
        }

        // Do not reveal that a private service-to-service endpoint exists.
        exchange.getResponse().setStatusCode(HttpStatus.NOT_FOUND);
        return exchange.getResponse().setComplete();
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
