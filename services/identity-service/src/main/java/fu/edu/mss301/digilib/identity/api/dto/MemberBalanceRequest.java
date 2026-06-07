package fu.edu.mss301.digilib.identity.api.dto;

import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public record MemberBalanceRequest(@Positive BigDecimal amount) {
}
