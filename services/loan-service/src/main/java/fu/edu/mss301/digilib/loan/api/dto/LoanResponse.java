package fu.edu.mss301.digilib.loan.api.dto;

import fu.edu.mss301.digilib.loan.domain.aggregate.Loan;
import fu.edu.mss301.digilib.loan.domain.vo.LoanStatus;

import java.time.LocalDateTime;

public record LoanResponse(
        Long loanId,
        Long memberId,
        Long bookId,
        String bookType,
        LoanStatus status,
        LocalDateTime dueDate,
        LocalDateTime returnedAt
) {
    public static LoanResponse from(Loan loan) {
        return new LoanResponse(
                loan.getLoanId(),
                loan.getMemberId(),
                loan.getBookId(),
                loan.getBookType(),
                loan.getStatus(),
                loan.getDueDate(),
                loan.getReturnedAt()
        );
    }
}
