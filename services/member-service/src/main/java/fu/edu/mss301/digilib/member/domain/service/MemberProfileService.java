package fu.edu.mss301.digilib.member.domain.service;

import fu.edu.mss301.digilib.member.domain.entity.MemberProfile;
import fu.edu.mss301.digilib.member.domain.repository.MemberProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MemberProfileService {

    private final MemberProfileRepository repository;

    public Mono<MemberProfile> getProfileById(String id) {
        return repository.findById(id);
    }

    public Mono<MemberProfile> registerOrFetchProfile(String id, String email, String firstName, String lastName) {
        return repository.findById(id)
                .switchIfEmpty(Mono.defer(() -> {
                    // JIT Profile Generation logic for fresh accounts
                    MemberProfile newProfile = MemberProfile.builder()
                            .id(id)
                            .email(email)
                            .firstName(firstName != null ? firstName : "Library")
                            .lastName(lastName != null ? lastName : "Member")
                            .memberType("READER")
                            .memberCode("LIB-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                            .borrowingLimit(5)
                            .loanPeriodDays(14)
                            .outstandingBalance(BigDecimal.ZERO)
                            .createdAt(Instant.now())
                            .updatedAt(Instant.now())
                            .isNewRecord(true) // Crucial flag for R2DBC manual ID mapping
                            .build();
                    return repository.save(newProfile);
                }));
    }
}
