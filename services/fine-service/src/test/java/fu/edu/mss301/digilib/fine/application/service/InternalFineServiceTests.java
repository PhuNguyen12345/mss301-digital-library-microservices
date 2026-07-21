package fu.edu.mss301.digilib.fine.application.service;

import fu.edu.mss301.digilib.fine.api.dto.FineResponse;
import fu.edu.mss301.digilib.fine.api.dto.OverdueReturnFineRequest;
import fu.edu.mss301.digilib.fine.domain.entity.Fine;
import fu.edu.mss301.digilib.fine.domain.entity.FinePolicy;
import fu.edu.mss301.digilib.fine.domain.vo.FineReason;
import fu.edu.mss301.digilib.fine.domain.vo.FineStatus;
import fu.edu.mss301.digilib.fine.infrastructure.persistence.FineJpaRepository;
import fu.edu.mss301.digilib.fine.infrastructure.persistence.FinePolicyJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InternalFineServiceTests {

    @Mock
    private FineJpaRepository fineRepository;

    @Mock
    private FinePolicyJpaRepository finePolicyRepository;

    private InternalFineService service;

    @BeforeEach
    void setUp() {
        service = new InternalFineService(fineRepository, finePolicyRepository);
    }

    @Test
    void createsFineUsingActivePolicy() {
        OverdueReturnFineRequest request = request();
        when(fineRepository.findByLoanIdForUpdate(7L)).thenReturn(Optional.empty());
        when(finePolicyRepository.findFirstByIsActiveTrueOrderByIdDesc()).thenReturn(Optional.of(policy()));
        when(fineRepository.save(any(Fine.class))).thenAnswer(invocation -> invocation.getArgument(0));

        FineResponse response = service.createOverdueReturnFine(request);

        assertEquals(20_000L, response.amountDue());
        assertEquals(FineReason.OVERDUE_RETURN, response.reason());
        assertEquals(FineStatus.PENDING, response.status());
    }

    @Test
    void returnsExistingOverdueFineForIdempotentRetry() {
        Fine existing = fine(FineReason.OVERDUE_RETURN);
        when(fineRepository.findByLoanIdForUpdate(7L)).thenReturn(Optional.of(existing));

        FineResponse response = service.createOverdueReturnFine(request());

        assertEquals(20_000L, response.amountDue());
        verify(fineRepository, never()).save(any());
        verify(finePolicyRepository, never()).findFirstByIsActiveTrueOrderByIdDesc();
    }

    @Test
    void convertsPendingThresholdFineToFinalReturnFine() {
        Fine existing = fine(FineReason.OVERDUE_THRESHOLD);
        existing.setReturnDate(null);
        when(fineRepository.findByLoanIdForUpdate(7L)).thenReturn(Optional.of(existing));
        when(fineRepository.save(existing)).thenReturn(existing);

        FineResponse response = service.createOverdueReturnFine(request());

        assertEquals(FineReason.OVERDUE_RETURN, response.reason());
        assertEquals(20_000L, response.amountDue());
        assertEquals(LocalDate.of(2026, 7, 5).atStartOfDay(), response.returnDate());
    }

    private OverdueReturnFineRequest request() {
        return new OverdueReturnFineRequest(
                "student-1", "7", "11", "12", "Clean Code", 250_000L,
                LocalDate.of(2026, 7, 1), LocalDate.of(2026, 7, 5), 4L);
    }

    private FinePolicy policy() {
        return FinePolicy.builder()
                .id(1)
                .dailyRate(5_000L)
                .isActive(true)
                .lostThresholdDays(30)
                .lostPenalty(0L)
                .build();
    }

    private Fine fine(FineReason reason) {
        return Fine.builder()
                .id(1)
                .finePolicy(policy())
                .loanId(7L)
                .bookId(11L)
                .studentId("student-1")
                .reason(reason)
                .dueDate(LocalDate.of(2026, 7, 1).atStartOfDay())
                .returnDate(LocalDate.of(2026, 7, 5).atStartOfDay())
                .amountDue(20_000L)
                .compensationAmount(0L)
                .status(FineStatus.PENDING)
                .createAt(LocalDateTime.now())
                .updateAt(LocalDateTime.now())
                .build();
    }
}
