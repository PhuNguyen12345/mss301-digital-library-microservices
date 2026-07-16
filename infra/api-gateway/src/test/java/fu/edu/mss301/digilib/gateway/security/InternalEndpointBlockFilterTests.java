package fu.edu.mss301.digilib.gateway.security;

import org.junit.jupiter.api.Test;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpStatus;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.assertj.core.api.Assertions.assertThat;

class InternalEndpointBlockFilterTests {

    private final InternalEndpointBlockFilter filter = new InternalEndpointBlockFilter();

    @Test
    void returnsNotFoundForInternalMemberEndpoint() {
        MockServerWebExchange exchange = exchangeFor("/api/v1/members/internal/member-1");
        AtomicBoolean downstreamCalled = new AtomicBoolean(false);

        filter.filter(exchange, trackingChain(downstreamCalled)).block();

        assertThat(exchange.getResponse().getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(downstreamCalled).isFalse();
    }

    @Test
    void allowsPublicMemberEndpoint() {
        MockServerWebExchange exchange = exchangeFor("/api/v1/members/member-1");
        AtomicBoolean downstreamCalled = new AtomicBoolean(false);

        filter.filter(exchange, trackingChain(downstreamCalled)).block();

        assertThat(exchange.getResponse().getStatusCode()).isNull();
        assertThat(downstreamCalled).isTrue();
    }

    @Test
    void doesNotBlockSimilarButNonInternalPrefix() {
        MockServerWebExchange exchange = exchangeFor("/api/v1/members/internalized/member-1");
        AtomicBoolean downstreamCalled = new AtomicBoolean(false);

        filter.filter(exchange, trackingChain(downstreamCalled)).block();

        assertThat(downstreamCalled).isTrue();
    }

    private MockServerWebExchange exchangeFor(String path) {
        return MockServerWebExchange.from(MockServerHttpRequest.get(path).build());
    }

    private GatewayFilterChain trackingChain(AtomicBoolean downstreamCalled) {
        return exchange -> {
            downstreamCalled.set(true);
            return Mono.empty();
        };
    }
}
