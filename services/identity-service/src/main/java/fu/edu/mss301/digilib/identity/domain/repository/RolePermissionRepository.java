package fu.edu.mss301.digilib.identity.domain.repository;

import fu.edu.mss301.digilib.identity.domain.entity.RolePermission;
import java.util.UUID;

public interface RolePermissionRepository {

	<S extends RolePermission> S save(S rolePermission);

	boolean existsByRoleRoleIdAndPermissionPermissionId(UUID roleId, UUID permissionId);
}
