package fu.edu.mss301.digilib.identity.infrastructure.persistence;

import fu.edu.mss301.digilib.identity.domain.entity.Permission;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PermissionJpaRepository extends JpaRepository<Permission, UUID> {

	boolean existsByPermissionName(String permissionName);
}
