package fu.edu.mss301.digilib.member.config;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class InternalApiKeyWebFilterTests {

    private static final String INTERNAL_API_KEY =
            "test-only-internal-api-key-with-more-than-32-characters";

    private final InternalApiKeyWebFilter filter = new InternalApiKeyWebFilter(INTERNAL_API_KEY);

    @Test
    void rejectsInternalRequestWhenApiKeyIsMissing() {
        MockServerWebExchange exchange = exchangeFor("/api/v1/members/internal/member-1", null);
        AtomicBoolean downstreamCalled = new AtomicBoolean(false);

        StepVerifier.create(filter.filter(exchange, trackingChain(downstreamCalled)))
                .verifyComplete();

        assertThat(exchange.getResponse().getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(downstreamCalled).isFalse();
    }

    @Test
    void rejectsInternalRequestWhenApiKeyIsWrong() {
        MockServerWebExchange exchange = exchangeFor(
                "/api/v1/members/internal/member-1",
                "wrong-internal-api-key-with-more-than-32-characters");
        AtomicBoolean downstreamCalled = new AtomicBoolean(false);

        StepVerifier.create(filter.filter(exchange, trackingChain(downstreamCalled)))
                .verifyComplete();

        assertThat(exchange.getResponse().getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(downstreamCalled).isFalse();
    }

    @Test
    void allowsInternalRequestWhenApiKeyMatches() {
        MockServerWebExchange exchange = exchangeFor(
                "/api/v1/members/internal/member-1",
                INTERNAL_API_KEY);
        AtomicBoolean downstreamCalled = new AtomicBoolean(false);

        StepVerifier.create(filter.filter(exchange, trackingChain(downstreamCalled)))
                .verifyComplete();

        assertThat(exchange.getResponse().getStatusCode()).isNull();
        assertThat(downstreamCalled).isTrue();
    }

    @Test
    void doesNotRequireInternalKeyForPublicMemberPath() {
        MockServerWebExchange exchange = exchangeFor("/api/v1/members/me", null);
        AtomicBoolean downstreamCalled = new AtomicBoolean(false);

        StepVerifier.create(filter.filter(exchange, trackingChain(downstreamCalled)))
                .verifyComplete();

        assertThat(downstreamCalled).isTrue();
    }

    @Test
    void rejectsPlaceholderOrShortConfiguration() {
        assertThatThrownBy(() -> new InternalApiKeyWebFilter("replace-with-a-random-key"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("INTERNAL_API_KEY");
        assertThatThrownBy(() -> new InternalApiKeyWebFilter("too-short"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("INTERNAL_API_KEY");
    }

    private MockServerWebExchange exchangeFor(String path, String internalApiKey) {
        MockServerHttpRequest.BaseBuilder<?> request = MockServerHttpRequest.get(path);
        if (internalApiKey != null) {
            request.header(InternalApiKeyWebFilter.HEADER_NAME, internalApiKey);
        }
        return MockServerWebExchange.from(request.build());
    }

    private WebFilterChain trackingChain(AtomicBoolean downstreamCalled) {
        return exchange -> {
            downstreamCalled.set(true);
            return Mono.empty();
        };
    }
}
