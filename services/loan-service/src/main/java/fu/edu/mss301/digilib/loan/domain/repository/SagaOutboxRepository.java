package fu.edu.mss301.digilib.loan.domain.repository;

import fu.edu.mss301.digilib.loan.domain.entity.SagaOutbox;
import fu.edu.mss301.digilib.loan.domain.vo.OutboxStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SagaOutboxRepository extends JpaRepository<SagaOutbox, Long> {
    List<SagaOutbox> findTop50ByStatusOrderByCreatedAtAsc(OutboxStatus status);
}
