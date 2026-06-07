package fu.edu.mss301.digilib.identity.domain.aggregate;

import fu.edu.mss301.digilib.identity.domain.entity.User;
import java.time.LocalDateTime;

public final class AuthenticationSessionAggregate {

	private final User user;

	private AuthenticationSessionAggregate(User user) {
		this.user = user;
	}

	public static AuthenticationSessionAggregate from(User user) {
		return new AuthenticationSessionAggregate(user);
	}

	public void recordSuccessfulLogin(LocalDateTime loginAt) {
		user.recordSuccessfulLogin(loginAt);
	}

	public boolean canAuthenticate() {
		return user.hasStatus("ACTIVE");
	}

	public User user() {
		return user;
	}
}
