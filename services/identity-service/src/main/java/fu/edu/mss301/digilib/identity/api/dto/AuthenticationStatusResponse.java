package fu.edu.mss301.digilib.identity.api.dto;

import fu.edu.mss301.digilib.identity.domain.entity.User;
import java.time.LocalDateTime;
import java.util.UUID;

public record AuthenticationStatusResponse(UUID userId, String username, boolean active, String authType,
		LocalDateTime lastLogin) {

	public static AuthenticationStatusResponse from(User user) {
		return new AuthenticationStatusResponse(user.getUserId(), user.getUsername(), user.hasStatus("ACTIVE"),
				user.getAuthType(), user.getLastLogin());
	}
}
