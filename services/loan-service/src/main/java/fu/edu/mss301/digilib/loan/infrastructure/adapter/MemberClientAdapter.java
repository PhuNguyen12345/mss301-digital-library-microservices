package fu.edu.mss301.digilib.loan.infrastructure.adapter;

import lombok.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class MemberClientAdapter {
    private final RestClient restClient;

    public MemberClientAdapter(
            RestClient.Builder restClientBuilder,
//            @Value("${services.member.base-url}")
            String memberServiceBaseUrl
    ) {
        this.restClient = restClientBuilder
                .baseUrl(memberServiceBaseUrl)
                .build();
    }

    public boolean isEligible(Long memberId) {
        MemberEligibilityResponse response = restClient.get()
                .uri("/api/v1/members/{memberId}/eligibility", memberId)
                .retrieve()
                .body(MemberEligibilityResponse.class);

        return response != null && response.eligible();
    }

    private record MemberEligibilityResponse(
            boolean eligible
    ) {
    }
}