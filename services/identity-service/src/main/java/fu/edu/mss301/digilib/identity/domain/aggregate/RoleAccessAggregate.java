package fu.edu.mss301.digilib.identity.domain.aggregate;

import fu.edu.mss301.digilib.identity.domain.entity.Permission;
import fu.edu.mss301.digilib.identity.domain.entity.Role;
import fu.edu.mss301.digilib.identity.domain.entity.RolePermission;
import fu.edu.mss301.digilib.identity.domain.vo.PermissionScope;

public final class RoleAccessAggregate {

	private final Role role;

	private RoleAccessAggregate(Role role) {
		this.role = role;
	}

	public static RoleAccessAggregate from(Role role) {
		return new RoleAccessAggregate(role);
	}

	public static RoleAccessAggregate createRole(String roleName, String description) {
		return new RoleAccessAggregate(new Role(roleName, description));
	}

	public static Permission createPermission(String permissionName, PermissionScope scope) {
		return new Permission(permissionName, scope.resource(), scope.action());
	}

	public RolePermission assignPermission(Permission permission) {
		return new RolePermission(role, permission);
	}

	public Role role() {
		return role;
	}
}
