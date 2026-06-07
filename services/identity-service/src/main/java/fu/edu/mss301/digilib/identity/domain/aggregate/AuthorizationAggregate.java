package fu.edu.mss301.digilib.identity.domain.aggregate;

import fu.edu.mss301.digilib.identity.domain.entity.Permission;
import fu.edu.mss301.digilib.identity.domain.entity.Role;
import fu.edu.mss301.digilib.identity.domain.entity.User;
import fu.edu.mss301.digilib.identity.domain.vo.PermissionScope;
import java.util.Set;
import java.util.stream.Collectors;

public final class AuthorizationAggregate {

	private final User user;

	private AuthorizationAggregate(User user) {
		this.user = user;
	}

	public static AuthorizationAggregate from(User user) {
		return new AuthorizationAggregate(user);
	}

	public boolean hasRole(String roleName) {
		return roles().stream().anyMatch(role -> role.getRoleName().equalsIgnoreCase(roleName));
	}

	public boolean canAccess(PermissionScope scope) {
		return permissions().stream().anyMatch(permission -> matches(permission, scope));
	}

	public Set<Role> roles() {
		return user.getUserRoles().stream()
				.map(userRole -> userRole.getRole())
				.collect(Collectors.toUnmodifiableSet());
	}

	public Set<Permission> permissions() {
		return roles().stream()
				.flatMap(role -> role.getRolePermissions().stream())
				.map(rolePermission -> rolePermission.getPermission())
				.collect(Collectors.toUnmodifiableSet());
	}

	private boolean matches(Permission permission, PermissionScope scope) {
		return permission.getResource().equalsIgnoreCase(scope.resource())
				&& permission.getAction().equalsIgnoreCase(scope.action());
	}
}
