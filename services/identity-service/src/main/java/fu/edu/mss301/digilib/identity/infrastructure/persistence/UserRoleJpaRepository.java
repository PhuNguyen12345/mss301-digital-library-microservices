package fu.edu.mss301.digilib.identity.infrastructure.persistence;

import fu.edu.mss301.digilib.identity.domain.entity.UserRole;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRoleJpaRepository extends JpaRepository<UserRole, UUID> {

	boolean existsByUserUserIdAndRoleRoleId(UUID userId, UUID roleId);
}
