package fu.edu.mss301.digilib.member.infrastructure.persistence;

import fu.edu.mss301.digilib.member.domain.entity.MemberProfile;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface MemberR2dbcRepository extends ReactiveCrudRepository<MemberProfile, String> {
    Mono<MemberProfile> findByEmail(String email);
}
