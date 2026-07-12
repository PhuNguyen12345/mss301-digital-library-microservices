package fu.edu.mss301.digilib.member.infrastructure.persistence;

import fu.edu.mss301.digilib.member.domain.entity.MemberProfile;
import fu.edu.mss301.digilib.member.domain.repository.MemberProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class MemberPersistenceAdapter implements MemberProfileRepository {

    private final MemberR2dbcRepository r2dbcRepository;

    @Override
    public Flux<MemberProfile> findAll() {
        return r2dbcRepository.findAll();
    }

    @Override
    public Mono<MemberProfile> findByEmail(String email) {
        return r2dbcRepository.findByEmail(email);
    }

    @Override
    public Mono<MemberProfile> findById(String id) {
        return r2dbcRepository.findById(id);
    }

    @Override
    public Mono<MemberProfile> save(MemberProfile memberProfile) {
        return r2dbcRepository.save(memberProfile);
    }
}
