package fu.edu.mss301.digilib.identity.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import fu.edu.mss301.digilib.identity.api.dto.PermissionCreateRequest;
import fu.edu.mss301.digilib.identity.api.dto.RoleCreateRequest;
import fu.edu.mss301.digilib.identity.api.dto.UserCreateRequest;
import fu.edu.mss301.digilib.identity.api.dto.UserResponse;
import fu.edu.mss301.digilib.identity.domain.entity.Permission;
import fu.edu.mss301.digilib.identity.domain.entity.Role;
import fu.edu.mss301.digilib.identity.domain.entity.User;
import fu.edu.mss301.digilib.identity.infrastructure.keycloak.KeycloakUserClient;
import fu.edu.mss301.digilib.identity.infrastructure.persistence.PermissionJpaRepository;
import fu.edu.mss301.digilib.identity.infrastructure.persistence.RoleJpaRepository;
import fu.edu.mss301.digilib.identity.infrastructure.persistence.RolePermissionJpaRepository;
import fu.edu.mss301.digilib.identity.infrastructure.persistence.UserJpaRepository;
import fu.edu.mss301.digilib.identity.infrastructure.persistence.UserRoleJpaRepository;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class IdentityServiceTests {

	@Mock
	UserJpaRepository userRepository;

	@Mock
	RoleJpaRepository roleRepository;

	@Mock
	PermissionJpaRepository permissionRepository;

	@Mock
	UserRoleJpaRepository userRoleRepository;

	@Mock
	RolePermissionJpaRepository rolePermissionRepository;

	@Mock
	KeycloakUserClient keycloakUserClient;

	IdentityService identityService;

	@BeforeEach
	void setUp() {
		identityService = new IdentityService(userRepository, roleRepository, permissionRepository, userRoleRepository,
				rolePermissionRepository, keycloakUserClient);
	}

	@Test
	void createUserProvisionsKeycloakAndPersistsLocalProfile() {
		UserCreateRequest request = request();
		when(keycloakUserClient.createUser(request)).thenReturn("kc-user-id");
		when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

		UserResponse response = identityService.createUser(request);

		verify(keycloakUserClient).createUser(request);
		verify(userRepository).save(any(User.class));
		assertThat(response.email()).isEqualTo(request.email());
		assertThat(response.member().memberCode()).isEqualTo(request.memberCode());
	}

	@Test
	void duplicateEmailStopsBeforeKeycloakProvisioning() {
		UserCreateRequest request = request();
		when(userRepository.existsByEmail(request.email())).thenReturn(true);

		assertThatThrownBy(() -> identityService.createUser(request))
				.isInstanceOf(DuplicateResourceException.class);

		verify(keycloakUserClient, never()).createUser(any());
	}

	@Test
	void assignRoleCreatesJoinRecordWhenMissing() {
		UUID userId = UUID.randomUUID();
		UUID roleId = UUID.randomUUID();
		User user = new User("a@example.com", "A", "User", null, null, "auser", "ACTIVE", "KEYCLOAK");
		Role role = new Role("MEMBER", "Member role");
		when(userRepository.findById(userId)).thenReturn(Optional.of(user));
		when(roleRepository.findById(roleId)).thenReturn(Optional.of(role));
		when(userRoleRepository.existsByUserUserIdAndRoleRoleId(userId, roleId)).thenReturn(false);
		when(userRepository.findWithRolesByUserId(userId)).thenReturn(Optional.of(user));

		identityService.assignRole(userId, roleId);

		verify(userRoleRepository).save(any());
	}

	@Test
	void assignPermissionCreatesJoinRecordWhenMissing() {
		UUID roleId = UUID.randomUUID();
		UUID permissionId = UUID.randomUUID();
		Role role = new Role("ADMIN", "Admin role");
		Permission permission = new Permission("BOOK_READ", "book", "read");
		when(roleRepository.findById(roleId)).thenReturn(Optional.of(role));
		when(permissionRepository.findById(permissionId)).thenReturn(Optional.of(permission));

		identityService.assignPermission(roleId, permissionId);

		verify(rolePermissionRepository).save(any());
	}

	@Test
	void createRoleRejectsDuplicateName() {
		when(roleRepository.existsByRoleName("ADMIN")).thenReturn(true);

		assertThatThrownBy(() -> identityService.createRole(new RoleCreateRequest("ADMIN", null)))
				.isInstanceOf(DuplicateResourceException.class);
	}

	@Test
	void createPermissionRejectsDuplicateName() {
		when(permissionRepository.existsByPermissionName("BOOK_READ")).thenReturn(true);

		assertThatThrownBy(() -> identityService
				.createPermission(new PermissionCreateRequest("BOOK_READ", "book", "read")))
				.isInstanceOf(DuplicateResourceException.class);
	}

	private UserCreateRequest request() {
		return new UserCreateRequest("user@example.com", "First", "Last", "0123456789", null, "user",
				"secret-password", "ACTIVE", "KEYCLOAK", "STANDARD", "M0001", 5, 14, BigDecimal.ZERO);
	}
}
