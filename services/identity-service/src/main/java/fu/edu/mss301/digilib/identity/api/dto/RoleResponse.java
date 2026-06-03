package fu.edu.mss301.digilib.identity.api.dto;

import fu.edu.mss301.digilib.identity.domain.entity.Role;
import java.util.UUID;

public record RoleResponse(UUID roleId, String roleName, String description) {

	public static RoleResponse from(Role role) {
		return new RoleResponse(role.getRoleId(), role.getRoleName(), role.getDescription());
	}
}
