package fu.edu.mss301.digilib.loan.application.usecase;

import fu.edu.mss301.digilib.loan.domain.aggregate.Loan;
import fu.edu.mss301.digilib.loan.domain.repository.LoanRepository;
import fu.edu.mss301.digilib.loan.domain.repository.SagaOutboxRepository;
import fu.edu.mss301.digilib.loan.infrastructure.adapter.BookCatalogClientAdapter;
import fu.edu.mss301.digilib.loan.infrastructure.adapter.FineClientAdapter;
import fu.edu.mss301.digilib.loan.infrastructure.adapter.MemberClientAdapter;
import fu.edu.mss301.digilib.loan.infrastructure.adapter.NotificationClientAdapter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ManageLoanUseCaseTests {

    @Mock
    private LoanRepository loanRepository;
    @Mock
    private SagaOutboxRepository outboxRepository;
    @Mock
    private BookCatalogClientAdapter catalogClient;
    @Mock
    private MemberClientAdapter memberClient;
    @Mock
    private FineClientAdapter fineClient;
    @Mock
    private NotificationClientAdapter notificationClient;

    @InjectMocks
    private ManageLoanUseCase useCase;

    @Test
    void sameReturnKeyReplaysWithoutRepeatingDownstreamSideEffects() {
        Loan returnedLoan = Loan.create(
                "member-1", 2L, 3L, "PHYSICAL",
                LocalDateTime.now().plusDays(14), "borrow-1");
        returnedLoan.returnBook("librarian-1", "return-1");
        when(loanRepository.findByIdForUpdate(7L)).thenReturn(Optional.of(returnedLoan));

        Loan result = useCase.returnBook(7L, "return-1", "librarian-1");

        assertSame(returnedLoan, result);
        verify(fineClient, never()).createOverdueReturnFine(
                org.mockito.ArgumentMatchers.any(),
                org.mockito.ArgumentMatchers.any(),
                org.mockito.ArgumentMatchers.anyLong());
        verify(catalogClient, never()).releaseBook(org.mockito.ArgumentMatchers.anyLong());
        verify(outboxRepository, never()).save(org.mockito.ArgumentMatchers.any());
        verify(notificationClient, never()).sendReturnConfirmation(
                org.mockito.ArgumentMatchers.any(),
                org.mockito.ArgumentMatchers.any(),
                org.mockito.ArgumentMatchers.any(),
                org.mockito.ArgumentMatchers.any(),
                org.mockito.ArgumentMatchers.any());
    }
}
