package fu.edu.mss301.digilib.loan.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ReturnLoanRequest(
        @NotNull Long loanId,
        @NotBlank String idempotencyKey
) {}
