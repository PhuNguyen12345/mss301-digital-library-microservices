package fu.edu.mss301.digilib.gateway.api.controller;

import org.junit.jupiter.api.Test;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.net.URI;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.TimeoutException;

import static org.assertj.core.api.Assertions.assertThat;

class FallbackControllerTests {

    private final FallbackController controller = new FallbackController();

    @Test
    void returnsGatewayTimeoutWithoutLeakingTheFailureMessage() {
        MockServerWebExchange exchange = exchangeFor("/fallback/catalog");
        exchange.getAttributes().put(
                ServerWebExchangeUtils.CIRCUITBREAKER_EXECUTION_EXCEPTION_ATTR,
                new IllegalStateException("wrapper", new TimeoutException("private upstream detail")));
        exchange.getAttributes().put(
                ServerWebExchangeUtils.GATEWAY_ORIGINAL_REQUEST_URL_ATTR,
                new LinkedHashSet<>(Set.of(URI.create("http://localhost:8080/api/catalog/books"))));

        ResponseEntity<FallbackController.GatewayFallbackResponse> response =
                controller.serviceFallback("catalog", exchange);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.GATEWAY_TIMEOUT);
        assertThat(response.getHeaders().getFirst(HttpHeaders.RETRY_AFTER)).isEqualTo("3");
        assertThat(response.getHeaders().getCacheControl()).isEqualTo("no-store");
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().code()).isEqualTo("UPSTREAM_TIMEOUT");
        assertThat(response.getBody().service()).isEqualTo("catalog");
        assertThat(response.getBody().path()).isEqualTo("/api/catalog/books");
        assertThat(response.getBody().message()).doesNotContain("private upstream detail");
    }

    @Test
    void returnsServiceUnavailableForAnOpenCircuitOrConnectionFailure() {
        MockServerWebExchange exchange = exchangeFor("/fallback/member");
        exchange.getAttributes().put(
                ServerWebExchangeUtils.CIRCUITBREAKER_EXECUTION_EXCEPTION_ATTR,
                new IllegalStateException("downstream unavailable"));

        ResponseEntity<FallbackController.GatewayFallbackResponse> response =
                controller.serviceFallback("member", exchange);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.SERVICE_UNAVAILABLE);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().code()).isEqualTo("UPSTREAM_UNAVAILABLE");
        assertThat(response.getBody().path()).isEqualTo("/fallback/member");
    }

    @Test
    void fallbackAcceptsNonGetRequestsWithoutReturningMethodNotAllowed() {
        WebTestClient.bindToController(controller)
                .build()
                .post()
                .uri("/fallback/member-auth")
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.SERVICE_UNAVAILABLE)
                .expectBody()
                .jsonPath("$.code").isEqualTo("UPSTREAM_UNAVAILABLE")
                .jsonPath("$.service").isEqualTo("member-auth");
    }

    private MockServerWebExchange exchangeFor(String path) {
        return MockServerWebExchange.from(MockServerHttpRequest.get(path).build());
    }
}
