package fu.edu.mss301.digilib.loan.application.command;

public record BorrowBookCommand(
        Long memberId,
        Long bookId,
        String bookType,
        String idempotencyKey
) {}
