package fu.edu.mss301.digilib.identity.domain.service;

import fu.edu.mss301.digilib.identity.domain.aggregate.AuthorizationAggregate;
import fu.edu.mss301.digilib.identity.domain.entity.User;
import fu.edu.mss301.digilib.identity.domain.vo.PermissionScope;
import org.springframework.stereotype.Service;

@Service
public class AuthorizationDomainService {

	public boolean canAccess(User user, PermissionScope scope) {
		if (!user.hasStatus("ACTIVE")) {
			return false;
		}
		return AuthorizationAggregate.from(user).canAccess(scope);
	}

	public boolean hasRole(User user, String roleName) {
		if (!user.hasStatus("ACTIVE")) {
			return false;
		}
		return AuthorizationAggregate.from(user).hasRole(roleName);
	}
}
