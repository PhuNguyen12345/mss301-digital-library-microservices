package fu.edu.mss301.digilib.loan.api.dto;

public record BorrowLoanRequest(
        Long memberId,
         Long bookId,
         String bookType,
         String idempotencyKey
) {}
