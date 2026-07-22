package fu.edu.mss301.digilib.fine.infrastructure.adapter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

/**
 * Fine Service does not own member data. This client is used only to resolve
 * a student's display name when listing fines for the librarian dashboard;
 * studentId itself comes from Loan Service at fine-creation time.
 */
@Slf4j
@Component
public class MemberClientAdapter {

    private final RestClient restClient;
    private final String internalApiKey;

    public MemberClientAdapter(
            RestClient.Builder restClientBuilder,
            @Value("${services.member.base-url}") String memberServiceBaseUrl,
            @Value("${services.internal-api-key}") String internalApiKey
    ) {
        this.internalApiKey = internalApiKey;
        this.restClient = restClientBuilder.baseUrl(memberServiceBaseUrl).build();
    }

    public MemberResponse getMember(String memberId) {
        try {
            return restClient.get()
                    .uri("/api/v1/members/internal/{memberId}", memberId)
                    .header("X-Internal-Api-Key", internalApiKey)
                    .retrieve()
                    .body(MemberResponse.class);
        } catch (RestClientException exception) {
            // Best-effort enrichment: Member Service being down or the member
            // being deleted should not break the fine history screen.
            log.warn("Could not resolve member details for memberId={}", memberId, exception);
            return null;
        }
    }

    public record MemberResponse(
            String id,
            String email,
            String firstName,
            String lastName
    ) {}
}
