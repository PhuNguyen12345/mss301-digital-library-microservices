package fu.edu.mss301.digilib.member.domain.repository;

import fu.edu.mss301.digilib.member.domain.entity.MemberProfile;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface MemberProfileRepository {
    Flux<MemberProfile> findAll();
    Mono<MemberProfile> findByEmail(String email);
    Mono<MemberProfile> findById(String id);
    Mono<MemberProfile> save(MemberProfile memberProfile);
}
