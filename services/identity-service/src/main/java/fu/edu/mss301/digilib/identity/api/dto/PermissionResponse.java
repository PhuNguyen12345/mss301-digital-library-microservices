package fu.edu.mss301.digilib.identity.api.dto;

import fu.edu.mss301.digilib.identity.domain.entity.Permission;
import java.util.UUID;

public record PermissionResponse(UUID permissionId, String permissionName, String resource, String action) {

	public static PermissionResponse from(Permission permission) {
		return new PermissionResponse(permission.getPermissionId(), permission.getPermissionName(),
				permission.getResource(), permission.getAction());
	}
}
