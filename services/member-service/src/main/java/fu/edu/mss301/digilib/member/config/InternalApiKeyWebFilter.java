package fu.edu.mss301.digilib.member.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import org.springframework.web.util.pattern.PathPattern;
import org.springframework.web.util.pattern.PathPatternParser;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class InternalApiKeyWebFilter implements WebFilter {

    static final String HEADER_NAME = "X-Internal-Api-Key";
    private static final int MINIMUM_KEY_LENGTH = 32;
    private static final PathPattern INTERNAL_MEMBER_PATH =
            new PathPatternParser().parse("/api/v1/members/internal/**");

    private final byte[] expectedApiKey;

    public InternalApiKeyWebFilter(@Value("${services.internal-api-key}") String internalApiKey) {
        if (!StringUtils.hasText(internalApiKey)
                || internalApiKey.length() < MINIMUM_KEY_LENGTH
                || internalApiKey.startsWith("replace-with-")) {
            throw new IllegalStateException(
                    "INTERNAL_API_KEY must be a non-placeholder secret with at least 32 characters");
        }
        this.expectedApiKey = internalApiKey.getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        if (!INTERNAL_MEMBER_PATH.matches(exchange.getRequest().getPath().pathWithinApplication())) {
            return chain.filter(exchange);
        }

        String suppliedApiKey = exchange.getRequest().getHeaders().getFirst(HEADER_NAME);
        if (!matches(suppliedApiKey)) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        return chain.filter(exchange);
    }

    private boolean matches(String suppliedApiKey) {
        return suppliedApiKey != null && MessageDigest.isEqual(
                expectedApiKey,
                suppliedApiKey.getBytes(StandardCharsets.UTF_8));
    }
}
