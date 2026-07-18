package fu.edu.mss301.digilib.gateway.security;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.cors.reactive.CorsWebFilter;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.assertj.core.api.Assertions.assertThat;

class GatewayCorsFilterTests {

    @Test
    void acceptsConfiguredPreflightOriginBeforeCallingDownstream() {
        GatewayCorsProperties properties = new GatewayCorsProperties();
        properties.setAllowedOrigins(List.of("http://localhost:5173"));
        CorsWebFilter filter = new GatewaySecurityConfig().corsWebFilter(properties);
        AtomicBoolean downstreamCalled = new AtomicBoolean(false);
        MockServerWebExchange exchange = MockServerWebExchange.from(
                MockServerHttpRequest.options("http://localhost:8080/api/v1/members/me")
                        .header(HttpHeaders.ORIGIN, "http://localhost:5173")
                        .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, HttpMethod.GET.name())
                        .build());

        filter.filter(exchange, ignored -> {
            downstreamCalled.set(true);
            return Mono.empty();
        }).block();

        assertThat(exchange.getResponse().getStatusCode()).isNull();
        assertThat(exchange.getResponse().getHeaders().getAccessControlAllowOrigin())
                .isEqualTo("http://localhost:5173");
        assertThat(downstreamCalled).isFalse();
    }
}
