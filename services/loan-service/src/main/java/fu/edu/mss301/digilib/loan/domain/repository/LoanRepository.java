package fu.edu.mss301.digilib.loan.domain.repository;

import fu.edu.mss301.digilib.loan.domain.aggregate.Loan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LoanRepository extends JpaRepository<Loan, Long> {
    Optional<Loan> findByIdempotencyKey(String idempotencyKey);
}
