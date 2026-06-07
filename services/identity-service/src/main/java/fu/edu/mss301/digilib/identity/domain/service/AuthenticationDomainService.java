package fu.edu.mss301.digilib.identity.domain.service;

import fu.edu.mss301.digilib.identity.domain.aggregate.AuthenticationSessionAggregate;
import fu.edu.mss301.digilib.identity.domain.entity.User;
import java.time.LocalDateTime;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationDomainService {

	public AuthenticationSessionAggregate recordSuccessfulLogin(User user, LocalDateTime loginAt) {
		AuthenticationSessionAggregate aggregate = AuthenticationSessionAggregate.from(user);
		if (!aggregate.canAuthenticate()) {
			throw new IllegalStateException("Only active users can authenticate");
		}
		aggregate.recordSuccessfulLogin(loginAt);
		return aggregate;
	}
}
