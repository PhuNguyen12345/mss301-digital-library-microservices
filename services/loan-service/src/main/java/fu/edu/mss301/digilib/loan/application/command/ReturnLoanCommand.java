package fu.edu.mss301.digilib.loan.application.command;

public record ReturnLoanCommand(
        Long loanId,
        String idempotencyKey
) {}
