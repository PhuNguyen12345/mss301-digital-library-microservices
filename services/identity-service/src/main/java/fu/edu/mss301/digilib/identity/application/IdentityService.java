package fu.edu.mss301.digilib.identity.application;

import fu.edu.mss301.digilib.identity.api.dto.PermissionCreateRequest;
import fu.edu.mss301.digilib.identity.api.dto.PermissionResponse;
import fu.edu.mss301.digilib.identity.api.dto.RoleCreateRequest;
import fu.edu.mss301.digilib.identity.api.dto.RoleResponse;
import fu.edu.mss301.digilib.identity.api.dto.UserCreateRequest;
import fu.edu.mss301.digilib.identity.api.dto.UserResponse;
import fu.edu.mss301.digilib.identity.domain.entity.Member;
import fu.edu.mss301.digilib.identity.domain.entity.Permission;
import fu.edu.mss301.digilib.identity.domain.entity.Role;
import fu.edu.mss301.digilib.identity.domain.entity.RolePermission;
import fu.edu.mss301.digilib.identity.domain.entity.User;
import fu.edu.mss301.digilib.identity.domain.entity.UserRole;
import fu.edu.mss301.digilib.identity.infrastructure.keycloak.KeycloakUserClient;
import fu.edu.mss301.digilib.identity.infrastructure.persistence.PermissionJpaRepository;
import fu.edu.mss301.digilib.identity.infrastructure.persistence.RoleJpaRepository;
import fu.edu.mss301.digilib.identity.infrastructure.persistence.RolePermissionJpaRepository;
import fu.edu.mss301.digilib.identity.infrastructure.persistence.UserJpaRepository;
import fu.edu.mss301.digilib.identity.infrastructure.persistence.UserRoleJpaRepository;
import java.math.BigDecimal;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class IdentityService {

	private final UserJpaRepository userRepository;
	private final RoleJpaRepository roleRepository;
	private final PermissionJpaRepository permissionRepository;
	private final UserRoleJpaRepository userRoleRepository;
	private final RolePermissionJpaRepository rolePermissionRepository;
	private final KeycloakUserClient keycloakUserClient;

	public IdentityService(UserJpaRepository userRepository, RoleJpaRepository roleRepository,
			PermissionJpaRepository permissionRepository, UserRoleJpaRepository userRoleRepository,
			RolePermissionJpaRepository rolePermissionRepository, KeycloakUserClient keycloakUserClient) {
		this.userRepository = userRepository;
		this.roleRepository = roleRepository;
		this.permissionRepository = permissionRepository;
		this.userRoleRepository = userRoleRepository;
		this.rolePermissionRepository = rolePermissionRepository;
		this.keycloakUserClient = keycloakUserClient;
	}

	@Transactional
	public UserResponse createUser(UserCreateRequest request) {
		validateUniqueUser(request);
		String keycloakUserId = keycloakUserClient.createUser(request);
		try {
			User user = new User(request.email(), request.firstName(), request.lastName(), request.phone(),
					request.avatarUrl(), request.username(), defaultString(request.userStatus(), "ACTIVE"),
					defaultString(request.authType(), "KEYCLOAK"));
			Member member = new Member(user, request.membershipType(), request.memberCode(), request.borrowingLimit(),
					request.loanPeriodDays(), request.outstandingBalance() == null ? BigDecimal.ZERO
							: request.outstandingBalance());
			user.setMember(member);
			return UserResponse.from(userRepository.save(user));
		}
		catch (RuntimeException ex) {
			keycloakUserClient.deleteUser(keycloakUserId);
			throw ex;
		}
	}

	@Transactional(readOnly = true)
	public UserResponse getUser(UUID userId) {
		return UserResponse.from(userRepository.findWithRolesByUserId(userId)
				.orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId)));
	}

	@Transactional
	public RoleResponse createRole(RoleCreateRequest request) {
		if (roleRepository.existsByRoleName(request.roleName())) {
			throw new DuplicateResourceException("Role name already exists: " + request.roleName());
		}
		return RoleResponse.from(roleRepository.save(new Role(request.roleName(), request.description())));
	}

	@Transactional
	public PermissionResponse createPermission(PermissionCreateRequest request) {
		if (permissionRepository.existsByPermissionName(request.permissionName())) {
			throw new DuplicateResourceException("Permission name already exists: " + request.permissionName());
		}
		return PermissionResponse.from(permissionRepository
				.save(new Permission(request.permissionName(), request.resource(), request.action())));
	}

	@Transactional
	public UserResponse assignRole(UUID userId, UUID roleId) {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));
		Role role = roleRepository.findById(roleId)
				.orElseThrow(() -> new ResourceNotFoundException("Role not found: " + roleId));
		if (!userRoleRepository.existsByUserUserIdAndRoleRoleId(userId, roleId)) {
			userRoleRepository.save(new UserRole(user, role));
		}
		return getUser(userId);
	}

	@Transactional
	public RoleResponse assignPermission(UUID roleId, UUID permissionId) {
		Role role = roleRepository.findById(roleId)
				.orElseThrow(() -> new ResourceNotFoundException("Role not found: " + roleId));
		Permission permission = permissionRepository.findById(permissionId)
				.orElseThrow(() -> new ResourceNotFoundException("Permission not found: " + permissionId));
		if (!rolePermissionRepository.existsByRoleRoleIdAndPermissionPermissionId(roleId, permissionId)) {
			rolePermissionRepository.save(new RolePermission(role, permission));
		}
		return RoleResponse.from(role);
	}

	private void validateUniqueUser(UserCreateRequest request) {
		if (userRepository.existsByEmail(request.email())) {
			throw new DuplicateResourceException("Email already exists: " + request.email());
		}
		if (StringUtils.hasText(request.phone()) && userRepository.existsByPhone(request.phone())) {
			throw new DuplicateResourceException("Phone already exists: " + request.phone());
		}
		if (userRepository.existsByUsername(request.username())) {
			throw new DuplicateResourceException("Username already exists: " + request.username());
		}
	}

	private String defaultString(String value, String fallback) {
		return StringUtils.hasText(value) ? value : fallback;
	}
}
