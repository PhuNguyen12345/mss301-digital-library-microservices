package fu.edu.mss301.digilib.identity.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import fu.edu.mss301.digilib.identity.api.dto.AuthorizationCheckRequest;
import fu.edu.mss301.digilib.identity.api.dto.AuthorizationCheckResponse;
import fu.edu.mss301.digilib.identity.api.dto.MemberBalanceRequest;
import fu.edu.mss301.digilib.identity.api.dto.MemberBorrowingEligibilityResponse;
import fu.edu.mss301.digilib.identity.api.dto.PermissionCreateRequest;
import fu.edu.mss301.digilib.identity.api.dto.RoleCreateRequest;
import fu.edu.mss301.digilib.identity.api.dto.UserCreateRequest;
import fu.edu.mss301.digilib.identity.api.dto.UserResponse;
import fu.edu.mss301.digilib.identity.domain.entity.Member;
import fu.edu.mss301.digilib.identity.domain.entity.Permission;
import fu.edu.mss301.digilib.identity.domain.entity.Role;
import fu.edu.mss301.digilib.identity.domain.entity.RolePermission;
import fu.edu.mss301.digilib.identity.domain.entity.User;
import fu.edu.mss301.digilib.identity.domain.entity.UserRole;
import fu.edu.mss301.digilib.identity.domain.repository.PermissionRepository;
import fu.edu.mss301.digilib.identity.domain.repository.RolePermissionRepository;
import fu.edu.mss301.digilib.identity.domain.repository.RoleRepository;
import fu.edu.mss301.digilib.identity.domain.repository.UserRepository;
import fu.edu.mss301.digilib.identity.domain.repository.UserRoleRepository;
import fu.edu.mss301.digilib.identity.domain.service.AuthenticationDomainService;
import fu.edu.mss301.digilib.identity.domain.service.AuthorizationDomainService;
import fu.edu.mss301.digilib.identity.domain.service.MemberManagementDomainService;
import fu.edu.mss301.digilib.identity.infrastructure.keycloak.KeycloakUserClient;
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
	UserRepository userRepository;

	@Mock
	RoleRepository roleRepository;

	@Mock
	PermissionRepository permissionRepository;

	@Mock
	UserRoleRepository userRoleRepository;

	@Mock
	RolePermissionRepository rolePermissionRepository;

	@Mock
	KeycloakUserClient keycloakUserClient;

	IdentityService identityService;

	@BeforeEach
	void setUp() {
		identityService = new IdentityService(userRepository, roleRepository, permissionRepository, userRoleRepository,
				rolePermissionRepository, keycloakUserClient, new AuthenticationDomainService(),
				new AuthorizationDomainService(), new MemberManagementDomainService());
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

	@Test
	void recordSuccessfulLoginUpdatesLastLoginForActiveUser() {
		UUID userId = UUID.randomUUID();
		User user = user();
		when(userRepository.findById(userId)).thenReturn(Optional.of(user));
		when(userRepository.save(user)).thenReturn(user);

		assertThat(identityService.recordSuccessfulLogin(userId).lastLogin()).isNotNull();
	}

	@Test
	void authorizationCheckGrantsWhenRoleHasPermission() {
		UUID userId = UUID.randomUUID();
		User user = user();
		Role role = new Role("LIBRARIAN", "Library staff");
		Permission permission = new Permission("BOOK_READ", "book", "read");
		new UserRole(user, role);
		new RolePermission(role, permission);
		when(userRepository.findWithRolesByUserId(userId)).thenReturn(Optional.of(user));

		AuthorizationCheckResponse response = identityService.checkAuthorization(userId,
				new AuthorizationCheckRequest("book", "read"));

		assertThat(response.granted()).isTrue();
	}

	@Test
	void chargeMemberIncreasesOutstandingBalance() {
		UUID userId = UUID.randomUUID();
		User user = userWithMember(BigDecimal.ZERO);
		when(userRepository.findById(userId)).thenReturn(Optional.of(user));
		when(userRepository.save(user)).thenReturn(user);

		assertThat(identityService.chargeMember(userId, new MemberBalanceRequest(BigDecimal.TEN))
				.outstandingBalance()).isEqualByComparingTo(BigDecimal.TEN);
	}

	@Test
	void borrowingEligibilityUsesMemberLimitAndActiveLoans() {
		UUID userId = UUID.randomUUID();
		User user = userWithMember(BigDecimal.ZERO);
		when(userRepository.findById(userId)).thenReturn(Optional.of(user));

		MemberBorrowingEligibilityResponse response = identityService.checkBorrowingEligibility(userId, 4);

		assertThat(response.eligible()).isTrue();
		assertThat(response.availableSlots()).isEqualTo(1);
	}

	private UserCreateRequest request() {
		return new UserCreateRequest("user@example.com", "First", "Last", "0123456789", null, "user",
				"secret-password", "ACTIVE", "KEYCLOAK", "STANDARD", "M0001", 5, 14, BigDecimal.ZERO);
	}

	private User user() {
		return new User("a@example.com", "A", "User", null, null, "auser", "ACTIVE", "KEYCLOAK");
	}

	private User userWithMember(BigDecimal outstandingBalance) {
		User user = user();
		user.setMember(new Member(user, "STANDARD", "M0001", 5, 14, outstandingBalance));
		return user;
	}
}
