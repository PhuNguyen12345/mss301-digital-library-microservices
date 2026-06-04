package fu.edu.mss301.digilib.identity.infrastructure.persistence;

import fu.edu.mss301.digilib.identity.domain.entity.RolePermission;
import fu.edu.mss301.digilib.identity.domain.repository.RolePermissionRepository;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RolePermissionJpaRepository extends JpaRepository<RolePermission, UUID>, RolePermissionRepository {

	boolean existsByRoleRoleIdAndPermissionPermissionId(UUID roleId, UUID permissionId);
}
