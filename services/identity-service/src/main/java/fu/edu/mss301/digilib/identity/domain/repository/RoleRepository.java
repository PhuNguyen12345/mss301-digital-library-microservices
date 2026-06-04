package fu.edu.mss301.digilib.identity.domain.repository;

import fu.edu.mss301.digilib.identity.domain.entity.Role;
import java.util.Optional;
import java.util.UUID;

public interface RoleRepository {

	<S extends Role> S save(S role);

	Optional<Role> findById(UUID roleId);

	boolean existsByRoleName(String roleName);
}
