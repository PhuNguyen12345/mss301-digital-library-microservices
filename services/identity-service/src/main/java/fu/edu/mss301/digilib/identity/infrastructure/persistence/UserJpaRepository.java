package fu.edu.mss301.digilib.identity.infrastructure.persistence;

import fu.edu.mss301.digilib.identity.domain.entity.User;
import java.util.UUID;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserJpaRepository extends JpaRepository<User, UUID> {

	boolean existsByEmail(String email);

	boolean existsByPhone(String phone);

	boolean existsByUsername(String username);

	@EntityGraph(attributePaths = { "member", "userRoles", "userRoles.role", "userRoles.role.rolePermissions",
			"userRoles.role.rolePermissions.permission" })
	java.util.Optional<User> findWithRolesByUserId(UUID userId);
}
