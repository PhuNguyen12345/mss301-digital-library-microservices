package fu.edu.mss301.digilib.loan.api.dto;

public record ReturnLoanRequest(
        String idempotencyKey
) {}
