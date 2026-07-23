package fu.edu.mss301.digilib.notification.infrastructure.client;

import feign.RequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

final class LoanServiceFeignConfig {

    private static final String INTERNAL_API_KEY_HEADER = "X-Internal-Api-Key";

    @Bean
    RequestInterceptor loanServiceInternalApiKeyInterceptor(
            @Value("${services.internal-api-key}") String internalApiKey) {
        return template -> template.header(INTERNAL_API_KEY_HEADER, internalApiKey);
    }
}
