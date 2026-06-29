package fu.edu.mss301.digilib.member.api.controller;

import fu.edu.mss301.digilib.member.api.dto.MemberResponse;
import fu.edu.mss301.digilib.member.api.dto.MemberUpdateRequest;
import fu.edu.mss301.digilib.member.domain.service.MemberProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/members")
public class MemberProfileController {

    private final MemberProfileService profileService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public Flux<MemberResponse> getAllMember() {
        return profileService.getAll()
                .switchIfEmpty(Flux.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Member list is empty")))
                .map(MemberResponse::from);
    }

    /**
     * Retrieves or creates a profile dynamically based on the validated identity context.
     */
    @GetMapping("/me")
    public Mono<MemberResponse> getMyProfile(@AuthenticationPrincipal Jwt jwt) {
        String keycloakSub = jwt.getSubject();
        String email = jwt.getClaimAsString("email");
        String givenName = jwt.getClaimAsString("given_name");
        String familyName = jwt.getClaimAsString("family_name");
        String firstName = givenName != null ? givenName.trim() : null;
        String lastName = familyName != null ? familyName.trim() : null;

        // 1. Determine user type (Lecturer vs Student vs Librarian vs Admin)
        java.util.Map<String, Object> realmAccess = jwt.getClaim("realm_access");
        java.util.List<String> roles = realmAccess != null ? (java.util.List<String>) realmAccess.get("roles") : java.util.Collections.emptyList();

        String memberType = "STUDENT";
        if (roles.contains("admin")) {
            memberType = "ADMIN";
        } else if (roles.contains("librarian")) {
            memberType = "LIBRARIAN";
        } else if (roles.contains("lecturer")) {
            memberType = "LECTURER";
        }

        // 2. Read role attributes mapped into token claims (with fallbacks)
        Integer borrowingLimit = jwt.getClaim("borrowing_limit") != null
                ? ((Number) jwt.getClaim("borrowing_limit")).intValue()
                : (memberType.equals("LECTURER") ? 10 : 5);

        Integer loanPeriodDays = jwt.getClaim("loan_period_days") != null
                ? ((Number) jwt.getClaim("loan_period_days")).intValue()
                : (memberType.equals("LECTURER") ? 30 : 14);

        return profileService.registerOrFetchProfile(
                        keycloakSub,
                        email,
                        firstName,
                        lastName,
                        memberType,
                        borrowingLimit,
                        loanPeriodDays
                )
                .map(MemberResponse::from);
    }

    @PatchMapping("/me")
    public Mono<MemberResponse> updateMyProfile(@AuthenticationPrincipal Jwt jwt,
                                                @Valid @RequestBody MemberUpdateRequest request) {
        return profileService.updateProfile(
                        jwt.getSubject(),
                        request.firstName(),
                        request.lastName(),
                        request.phone(),
                        request.avatarKey()
                )
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Member profile not found")))
                .map(MemberResponse::from);
    }

    /**
     * Endpoint for internal inter-service communication (e.g., Loan Service checking borrowing capability)
     */
    @GetMapping("/{memberId}")
    public Mono<MemberResponse> getProfileById(@PathVariable String memberId) {
        return profileService.getProfileById(memberId)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Member profile not found")))
                .map(MemberResponse::from);
    }
}
