package fu.edu.mss301.digilib.identity.domain.repository;

import fu.edu.mss301.digilib.identity.domain.entity.Permission;
import java.util.Optional;
import java.util.UUID;

public interface PermissionRepository {

	<S extends Permission> S save(S permission);

	Optional<Permission> findById(UUID permissionId);

	boolean existsByPermissionName(String permissionName);
}
