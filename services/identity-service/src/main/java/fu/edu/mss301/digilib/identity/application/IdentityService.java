package fu.edu.mss301.digilib.identity.application;

import fu.edu.mss301.digilib.identity.api.dto.PermissionCreateRequest;
import fu.edu.mss301.digilib.identity.api.dto.PermissionResponse;
import fu.edu.mss301.digilib.identity.api.dto.RoleCreateRequest;
import fu.edu.mss301.digilib.identity.api.dto.RoleResponse;
import fu.edu.mss301.digilib.identity.api.dto.AuthenticationStatusResponse;
import fu.edu.mss301.digilib.identity.api.dto.AuthorizationCheckRequest;
import fu.edu.mss301.digilib.identity.api.dto.AuthorizationCheckResponse;
import fu.edu.mss301.digilib.identity.api.dto.MemberBalanceRequest;
import fu.edu.mss301.digilib.identity.api.dto.MemberBorrowingEligibilityResponse;
import fu.edu.mss301.digilib.identity.api.dto.MemberResponse;
import fu.edu.mss301.digilib.identity.api.dto.MemberUpdateRequest;
import fu.edu.mss301.digilib.identity.api.dto.UserCreateRequest;
import fu.edu.mss301.digilib.identity.api.dto.UserResponse;
import fu.edu.mss301.digilib.identity.domain.aggregate.MemberManagementAggregate;
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
import fu.edu.mss301.digilib.identity.domain.service.AuthenticationDomainService;
import fu.edu.mss301.digilib.identity.domain.service.AuthorizationDomainService;
import fu.edu.mss301.digilib.identity.domain.service.MemberManagementDomainService;
import fu.edu.mss301.digilib.identity.domain.vo.EmailAddress;
import fu.edu.mss301.digilib.identity.domain.vo.MemberCode;
import fu.edu.mss301.digilib.identity.domain.vo.PermissionScope;
import fu.edu.mss301.digilib.identity.domain.vo.PhoneNumber;
import fu.edu.mss301.digilib.identity.domain.vo.Username;
import fu.edu.mss301.digilib.identity.infrastructure.keycloak.KeycloakUserClient;
import java.time.LocalDateTime;
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
	private final AuthenticationDomainService authenticationDomainService;
	private final AuthorizationDomainService authorizationDomainService;
	private final MemberManagementDomainService memberManagementDomainService;

	public IdentityService(UserRepository userRepository, RoleRepository roleRepository,
			PermissionRepository permissionRepository, UserRoleRepository userRoleRepository,
			RolePermissionRepository rolePermissionRepository, KeycloakUserClient keycloakUserClient,
			AuthenticationDomainService authenticationDomainService,
			AuthorizationDomainService authorizationDomainService,
			MemberManagementDomainService memberManagementDomainService) {
		this.userRepository = userRepository;
		this.roleRepository = roleRepository;
		this.permissionRepository = permissionRepository;
		this.userRoleRepository = userRoleRepository;
		this.rolePermissionRepository = rolePermissionRepository;
		this.keycloakUserClient = keycloakUserClient;
		this.authenticationDomainService = authenticationDomainService;
		this.authorizationDomainService = authorizationDomainService;
		this.memberManagementDomainService = memberManagementDomainService;
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

	@Transactional
	public AuthenticationStatusResponse recordSuccessfulLogin(UUID userId) {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));
		authenticationDomainService.recordSuccessfulLogin(user, LocalDateTime.now());
		return AuthenticationStatusResponse.from(userRepository.save(user));
	}

	@Transactional
	public AuthenticationStatusResponse activateUser(UUID userId) {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));
		user.activate();
		return AuthenticationStatusResponse.from(userRepository.save(user));
	}

	@Transactional
	public AuthenticationStatusResponse suspendUser(UUID userId) {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));
		user.suspend();
		return AuthenticationStatusResponse.from(userRepository.save(user));
	}

	@Transactional(readOnly = true)
	public AuthorizationCheckResponse checkAuthorization(UUID userId, AuthorizationCheckRequest request) {
		User user = userRepository.findWithRolesByUserId(userId)
				.orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));
		PermissionScope scope = new PermissionScope(request.resource(), request.action());
		boolean granted = authorizationDomainService.canAccess(user, scope);
		return new AuthorizationCheckResponse(userId, scope.resource(), scope.action(), granted);
	}

	@Transactional
	public MemberResponse updateMember(UUID userId, MemberUpdateRequest request) {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));
		MemberManagementAggregate aggregate = memberManagementDomainService.updateMembership(user,
				request.membershipType(), request.borrowingLimit(), request.loanPeriodDays());
		return MemberResponse.from(userRepository.save(aggregate.user()).getMember());
	}

	@Transactional
	public MemberResponse chargeMember(UUID userId, MemberBalanceRequest request) {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));
		MemberManagementAggregate aggregate = memberManagementDomainService.charge(user, request.amount());
		return MemberResponse.from(userRepository.save(aggregate.user()).getMember());
	}

	@Transactional
	public MemberResponse receiveMemberPayment(UUID userId, MemberBalanceRequest request) {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));
		MemberManagementAggregate aggregate = memberManagementDomainService.receivePayment(user, request.amount());
		return MemberResponse.from(userRepository.save(aggregate.user()).getMember());
	}

	@Transactional(readOnly = true)
	public MemberBorrowingEligibilityResponse checkBorrowingEligibility(UUID userId, int activeLoans) {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));
		MemberManagementAggregate aggregate = MemberManagementAggregate.from(user);
		boolean eligible = memberManagementDomainService.canBorrow(user, activeLoans);
		return MemberBorrowingEligibilityResponse.from(userId, aggregate.member(), eligible, activeLoans,
				aggregate.availableBorrowingSlots(activeLoans));
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
