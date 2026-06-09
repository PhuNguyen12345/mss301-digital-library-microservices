package fu.edu.mss301.digilib.member.api.controller;

import fu.edu.mss301.digilib.member.domain.entity.MemberProfile;
import fu.edu.mss301.digilib.member.domain.service.MemberProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/members")
public class MemberProfileController {

    private final MemberProfileService profileService;

    /**
     * Retrieves or creates a profile dynamically based on the validated identity context.
     */
    @GetMapping("/me")
    public Mono<MemberProfile> getMyProfile(@AuthenticationPrincipal Jwt jwt) {
        String keycloakSub = jwt.getSubject(); // Keycloak's immutable User UUID
        String email = jwt.getClaimAsString("email");
        String firstName = jwt.getClaimAsString("given_name");
        String lastName = jwt.getClaimAsString("family_name");

        return profileService.registerOrFetchProfile(keycloakSub, email, firstName, lastName);
    }

    /**
     * Endpoint for internal inter-service communication (e.g., Loan Service checking borrowing capability)
     */
    @GetMapping("/{memberId}")
    public Mono<MemberProfile> getProfileById(@PathVariable String memberId) {
        return profileService.getProfileById(memberId);
    }
}