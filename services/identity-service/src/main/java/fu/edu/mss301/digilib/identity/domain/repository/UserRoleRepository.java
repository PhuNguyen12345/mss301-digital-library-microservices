package fu.edu.mss301.digilib.identity.domain.repository;

import fu.edu.mss301.digilib.identity.domain.entity.UserRole;
import java.util.UUID;

public interface UserRoleRepository {

	<S extends UserRole> S save(S userRole);

	boolean existsByUserUserIdAndRoleRoleId(UUID userId, UUID roleId);
}
