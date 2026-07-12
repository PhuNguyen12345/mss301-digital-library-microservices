package fu.edu.mss301.digilib.loan.application.usecase;

import fu.edu.mss301.digilib.loan.domain.aggregate.Loan;
import fu.edu.mss301.digilib.loan.domain.entity.SagaOutbox;
import fu.edu.mss301.digilib.loan.domain.repository.LoanRepository;
import fu.edu.mss301.digilib.loan.domain.repository.SagaOutboxRepository;
import fu.edu.mss301.digilib.loan.domain.vo.OutboxStatus;
import fu.edu.mss301.digilib.loan.infrastructure.adapter.BookCatalogClientAdapter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ManageLoanUseCase {

    private final LoanRepository loanRepository;
    private final SagaOutboxRepository outboxRepository;
    private final BookCatalogClientAdapter catalogClient;

    @Transactional(readOnly = true)
    public Loan findById(Long loanId) {
        return loanRepository.findById(loanId)
                .orElseThrow(() -> new IllegalArgumentException("Loan not found: " + loanId));
    }

    @Transactional(readOnly = true)
    public Page<Loan> findAll(Pageable pageable) {
        return loanRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public List<Loan> findByMember(String memberId) {
        return loanRepository.findByMemberIdOrderByBorrowedAtDesc(memberId);
    }

    @Transactional
    public Loan returnBook(Long loanId, String changedBy) {
        Loan loan = findById(loanId);
        boolean late = LocalDateTime.now().isAfter(loan.getDueDate());
        loan.returnBook(changedBy);
        Loan saved = loanRepository.save(loan);
        catalogClient.releaseBook(saved.getCopyId());
        outboxRepository.save(event(saved, late ? "BookReturnedLateEvent" : "BookReturnedEvent"));
        return saved;
    }

    @Transactional
    public Loan renew(Long loanId, String changedBy) {
        Loan loan = findById(loanId);
        loan.renew(changedBy);
        Loan saved = loanRepository.save(loan);
        outboxRepository.save(event(saved, "LoanRenewedEvent"));
        return saved;
    }

    private SagaOutbox event(Loan loan, String type) {
        return SagaOutbox.builder()
                .loanId(loan.getLoanId())
                .eventType(type)
                .payload("{\"loanId\":%d,\"memberId\":\"%s\",\"bookId\":%d}"
                        .formatted(loan.getLoanId(), loan.getMemberId(), loan.getBookId()))
                .status(OutboxStatus.PENDING)
                .retryCount(0)
                .createdAt(LocalDateTime.now())
                .build();
    }
}
