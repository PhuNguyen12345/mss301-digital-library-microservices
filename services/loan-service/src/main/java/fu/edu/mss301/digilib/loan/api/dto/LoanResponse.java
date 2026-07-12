package fu.edu.mss301.digilib.loan.api.dto;

import fu.edu.mss301.digilib.loan.domain.aggregate.Loan;
import fu.edu.mss301.digilib.loan.domain.vo.LoanStatus;

import java.time.LocalDateTime;

public record LoanResponse(
        Long loanId,
        String memberId,
        Long bookId,
        Long copyId,
        String bookType,
        LoanStatus status,
        LocalDateTime borrowedAt,
        LocalDateTime dueDate,
        LocalDateTime returnedAt,
        Integer renewalCount
) {
    public static LoanResponse from(Loan loan) {
        return new LoanResponse(
                loan.getLoanId(),
                loan.getMemberId(),
                loan.getBookId(),
                loan.getCopyId(),
                loan.getBookType(),
                loan.getStatus(),
                loan.getBorrowedAt(),
                loan.getDueDate(),
                loan.getReturnedAt(),
                loan.getRenewalCount()
        );
    }
}
