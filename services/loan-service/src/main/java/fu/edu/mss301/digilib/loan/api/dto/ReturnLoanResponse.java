package fu.edu.mss301.digilib.loan.api.dto;

public record ReturnLoanResponse(LoanResponse loan,
                                 boolean paymentRequired,
                                 FineSummary fine) {
    public record FineSummary(
            Integer fineId,
            Long amountDue,
            String status
    ) {
    }
}
