//package fu.edu.mss301.digilib.loan.application.usecase;
//
//import fu.edu.mss301.digilib.loan.application.command.BorrowBookCommand;
//import fu.edu.mss301.digilib.loan.domain.aggregate.Loan;
//import fu.edu.mss301.digilib.loan.domain.repository.LoanRepository;
//import fu.edu.mss301.digilib.loan.domain.repository.SagaOutboxRepository;
//import jakarta.transaction.Transactional;
//import org.springframework.stereotype.Service;
//
//@Service
//public class BorrowBookUseCase {
//    private final LoanRepository loanRepository;
//    private final SagaOutboxRepository outboxRepository;
//    private final MemberClientAdapter memberClient;
//    private final BookCatalogClientAdapter bookClient;
//
//    public BorrowBookUseCase(
//            LoanRepository loanRepository,
//            SagaOutboxRepository outboxRepository,
//            MemberClientAdapter memberClient,
//            BookCatalogClientAdapter bookClient
//    ) {
//        this.loanRepository = loanRepository;
//        this.outboxRepository = outboxRepository;
//        this.memberClient = memberClient;
//        this.bookClient = bookClient;
//    }
//
//    @Transactional
//    public Loan handle(BorrowBookCommand command) {
//        return loanRepository.findByIdempotencyKey(command.idempotencyKey())
//                .orElseGet(() -> borrow(command));
//    }
//
//    private Loan borrow(BorrowBookCommand command) {
//        if (!memberClient.isEligible(command.memberId())) {
//            throw new IllegalStateException("Member is not eligible");
//        }
//
//        if (!bookClient.reserveBook(command.bookId())) {
//            throw new IllegalStateException("Book is not available");
//        }
//
//        Instant now = Instant.now();
//
//        Loan loan = new Loan();
//        loan.setMemberId(command.memberId());
//        loan.setBookId(command.bookId());
//        loan.setBookType(command.bookType());
//        loan.setStatus(LoanStatus.BORROWED);
//        loan.setBorrowedAt(now);
//        loan.setDueDate(now.plus(14, ChronoUnit.DAYS));
//        loan.setIdempotencyKey(command.idempotencyKey());
//        loan.setCreatedAt(now);
//        loan.setUpdatedAt(now);
//
//        Loan saved = loanRepository.save(loan);
//
//        SagaOutbox event = new SagaOutbox();
//        event.setLoanId(saved.getLoanId());
//        event.setEventType("loan.borrowed");
//        event.setPayload("""
//                {"loanId":%d,"memberId":%d,"bookId":%d}
//                """.formatted(saved.getLoanId(), saved.getMemberId(), saved.getBookId()));
//        event.setStatus(OutboxStatus.PENDING);
//        event.setCreatedAt(now);
//
//        outboxRepository.save(event);
//        return saved;
//    }
//}

//xem service khac de giao tiep ntn