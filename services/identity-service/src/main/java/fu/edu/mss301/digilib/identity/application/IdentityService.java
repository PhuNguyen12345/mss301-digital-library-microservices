package fu.edu.mss301.digilib.identity.application;

import fu.edu.mss301.digilib.identity.api.dto.PermissionCreateRequest;
import fu.edu.mss301.digilib.identity.api.dto.PermissionResponse;
import fu.edu.mss301.digilib.identity.api.dto.RoleCreateRequest;
import fu.edu.mss301.digilib.identity.api.dto.RoleResponse;
import fu.edu.mss301.digilib.identity.api.dto.UserCreateRequest;
import fu.edu.mss301.digilib.identity.api.dto.UserResponse;
import fu.edu.mss301.digilib.identity.domain.aggregate.RoleAccessAggregate;
import fu.edu.mss301.digilib.identity.domain.aggregate.UserAccountAggregate;
import fu.edu.mss301.digilib.identity.domain.entity.Permission;
import fu.edu.mss301.digilib.identity.domain.entity.Role;
import fu.edu.mss301.digilib.identity.domain.entity.User;
import fu.edu.mss301.digilib.identity.domain.repository.PermissionRepository;
import fu.edu.mss301.digilib.identity.domain.repository.RolePermissionRepository;
import fu.edu.mss301.digilib.identity.domain.repository.RoleRepository;
import fu.edu.mss301.digilib.identity.domain.repository.UserRepository;
import fu.edu.mss301.digilib.identity.domain.repository.UserRoleRepository;
import fu.edu.mss301.digilib.identity.domain.vo.EmailAddress;
import fu.edu.mss301.digilib.identity.domain.vo.MemberCode;
import fu.edu.mss301.digilib.identity.domain.vo.PermissionScope;
import fu.edu.mss301.digilib.identity.domain.vo.PhoneNumber;
import fu.edu.mss301.digilib.identity.domain.vo.Username;
import fu.edu.mss301.digilib.identity.infrastructure.keycloak.KeycloakUserClient;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class IdentityService {

	private final UserRepository userRepository;
	private final RoleRepository roleRepository;
	private final PermissionRepository permissionRepository;
	private final UserRoleRepository userRoleRepository;
	private final RolePermissionRepository rolePermissionRepository;
	private final KeycloakUserClient keycloakUserClient;

	public IdentityService(UserRepository userRepository, RoleRepository roleRepository,
			PermissionRepository permissionRepository, UserRoleRepository userRoleRepository,
			RolePermissionRepository rolePermissionRepository, KeycloakUserClient keycloakUserClient) {
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
			UserAccountAggregate aggregate = UserAccountAggregate.create(new EmailAddress(request.email()),
					request.firstName(), request.lastName(), new PhoneNumber(request.phone()), request.avatarUrl(),
					new Username(request.username()), defaultString(request.userStatus(), "ACTIVE"),
					defaultString(request.authType(), "KEYCLOAK"), request.membershipType(),
					new MemberCode(request.memberCode()), request.borrowingLimit(), request.loanPeriodDays(),
					request.outstandingBalance());
			return UserResponse.from(userRepository.save(aggregate.user()));
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
		RoleAccessAggregate aggregate = RoleAccessAggregate.createRole(request.roleName(), request.description());
		return RoleResponse.from(roleRepository.save(aggregate.role()));
	}

	@Transactional
	public PermissionResponse createPermission(PermissionCreateRequest request) {
		if (permissionRepository.existsByPermissionName(request.permissionName())) {
			throw new DuplicateResourceException("Permission name already exists: " + request.permissionName());
		}
		Permission permission = RoleAccessAggregate.createPermission(request.permissionName(),
				new PermissionScope(request.resource(), request.action()));
		return PermissionResponse.from(permissionRepository.save(permission));
	}

	@Transactional
	public UserResponse assignRole(UUID userId, UUID roleId) {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));
		Role role = roleRepository.findById(roleId)
				.orElseThrow(() -> new ResourceNotFoundException("Role not found: " + roleId));
		if (!userRoleRepository.existsByUserUserIdAndRoleRoleId(userId, roleId)) {
			userRoleRepository.save(UserAccountAggregate.from(user).assignRole(role));
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
			rolePermissionRepository.save(RoleAccessAggregate.from(role).assignPermission(permission));
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
