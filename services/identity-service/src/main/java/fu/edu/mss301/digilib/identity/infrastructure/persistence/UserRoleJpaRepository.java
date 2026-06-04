package fu.edu.mss301.digilib.identity.infrastructure.persistence;

import fu.edu.mss301.digilib.identity.domain.entity.UserRole;
import fu.edu.mss301.digilib.identity.domain.repository.UserRoleRepository;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRoleJpaRepository extends JpaRepository<UserRole, UUID>, UserRoleRepository {

	boolean existsByUserUserIdAndRoleRoleId(UUID userId, UUID roleId);
}
