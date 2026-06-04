package fu.edu.mss301.digilib.identity.domain.repository;

import fu.edu.mss301.digilib.identity.domain.entity.User;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository {

	<S extends User> S save(S user);

	Optional<User> findById(UUID userId);

	Optional<User> findWithRolesByUserId(UUID userId);

	boolean existsByEmail(String email);

	boolean existsByPhone(String phone);

	boolean existsByUsername(String username);
}
