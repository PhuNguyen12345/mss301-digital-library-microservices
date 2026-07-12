package fu.edu.mss301.digilib.loan.api.dto;

import jakarta.validation.constraints.NotBlank;

public record ReturnLoanRequest(
        @NotBlank String idempotencyKey
) {}
