package fu.edu.mss301.digilib.identity.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;

public record UserCreateRequest(
		@NotBlank @Email String email,
		@NotBlank String firstName,
		@NotBlank String lastName,
		String phone,
		String avatarUrl,
		@NotBlank String username,
		@NotBlank String password,
		String userStatus,
		String authType,
		@NotBlank String membershipType,
		@NotBlank String memberCode,
		@PositiveOrZero Integer borrowingLimit,
		@PositiveOrZero Integer loanPeriodDays,
		@PositiveOrZero BigDecimal outstandingBalance) {
}
