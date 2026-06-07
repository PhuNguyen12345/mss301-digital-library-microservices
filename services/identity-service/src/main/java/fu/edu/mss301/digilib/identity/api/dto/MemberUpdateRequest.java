package fu.edu.mss301.digilib.identity.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;

public record MemberUpdateRequest(@NotBlank String membershipType, @PositiveOrZero Integer borrowingLimit,
		@PositiveOrZero Integer loanPeriodDays) {
}
