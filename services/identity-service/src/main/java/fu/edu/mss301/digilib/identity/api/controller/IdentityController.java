package fu.edu.mss301.digilib.identity.api.controller;

import fu.edu.mss301.digilib.identity.api.dto.AuthenticationStatusResponse;
import fu.edu.mss301.digilib.identity.api.dto.AuthorizationCheckRequest;
import fu.edu.mss301.digilib.identity.api.dto.AuthorizationCheckResponse;
import fu.edu.mss301.digilib.identity.api.dto.MemberBalanceRequest;
import fu.edu.mss301.digilib.identity.api.dto.MemberBorrowingEligibilityResponse;
import fu.edu.mss301.digilib.identity.api.dto.MemberResponse;
import fu.edu.mss301.digilib.identity.api.dto.MemberUpdateRequest;
import fu.edu.mss301.digilib.identity.api.dto.PermissionCreateRequest;
import fu.edu.mss301.digilib.identity.api.dto.PermissionResponse;
import fu.edu.mss301.digilib.identity.api.dto.RoleCreateRequest;
import fu.edu.mss301.digilib.identity.api.dto.RoleResponse;
import fu.edu.mss301.digilib.identity.api.dto.UserCreateRequest;
import fu.edu.mss301.digilib.identity.api.dto.UserResponse;
import fu.edu.mss301.digilib.identity.application.IdentityService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/identity")
public class IdentityController {

	private final IdentityService identityService;

	public IdentityController(IdentityService identityService) {
		this.identityService = identityService;
	}

	@PostMapping("/users")
	public ResponseEntity<UserResponse> createUser(@Valid @RequestBody UserCreateRequest request) {
		UserResponse response = identityService.createUser(request);
		return ResponseEntity.created(URI.create("/api/v1/identity/users/" + response.userId())).body(response);
	}

	@GetMapping("/users/{userId}")
	public UserResponse getUser(@PathVariable UUID userId) {
		return identityService.getUser(userId);
	}

	@PostMapping("/users/{userId}/roles/{roleId}")
	public UserResponse assignRole(@PathVariable UUID userId, @PathVariable UUID roleId) {
		return identityService.assignRole(userId, roleId);
	}

	@PostMapping("/users/{userId}/login-success")
	public AuthenticationStatusResponse recordSuccessfulLogin(@PathVariable UUID userId) {
		return identityService.recordSuccessfulLogin(userId);
	}

	@PostMapping("/users/{userId}/activate")
	public AuthenticationStatusResponse activateUser(@PathVariable UUID userId) {
		return identityService.activateUser(userId);
	}

	@PostMapping("/users/{userId}/suspend")
	public AuthenticationStatusResponse suspendUser(@PathVariable UUID userId) {
		return identityService.suspendUser(userId);
	}

	@PostMapping("/users/{userId}/authorization-checks")
	public AuthorizationCheckResponse checkAuthorization(@PathVariable UUID userId,
			@Valid @RequestBody AuthorizationCheckRequest request) {
		return identityService.checkAuthorization(userId, request);
	}

	@PutMapping("/users/{userId}/member")
	public MemberResponse updateMember(@PathVariable UUID userId, @Valid @RequestBody MemberUpdateRequest request) {
		return identityService.updateMember(userId, request);
	}

	@PostMapping("/users/{userId}/member/charges")
	public MemberResponse chargeMember(@PathVariable UUID userId, @Valid @RequestBody MemberBalanceRequest request) {
		return identityService.chargeMember(userId, request);
	}

	@PostMapping("/users/{userId}/member/payments")
	public MemberResponse receiveMemberPayment(@PathVariable UUID userId,
			@Valid @RequestBody MemberBalanceRequest request) {
		return identityService.receiveMemberPayment(userId, request);
	}

	@GetMapping("/users/{userId}/member/borrowing-eligibility")
	public MemberBorrowingEligibilityResponse checkBorrowingEligibility(@PathVariable UUID userId,
			@RequestParam(defaultValue = "0") int activeLoans) {
		return identityService.checkBorrowingEligibility(userId, activeLoans);
	}

	@PostMapping("/roles")
	public ResponseEntity<RoleResponse> createRole(@Valid @RequestBody RoleCreateRequest request) {
		RoleResponse response = identityService.createRole(request);
		return ResponseEntity.created(URI.create("/api/v1/identity/roles/" + response.roleId())).body(response);
	}

	@PostMapping("/permissions")
	public ResponseEntity<PermissionResponse> createPermission(@Valid @RequestBody PermissionCreateRequest request) {
		PermissionResponse response = identityService.createPermission(request);
		return ResponseEntity.created(URI.create("/api/v1/identity/permissions/" + response.permissionId()))
				.body(response);
	}

	@PostMapping("/roles/{roleId}/permissions/{permissionId}")
	public RoleResponse assignPermission(@PathVariable UUID roleId, @PathVariable UUID permissionId) {
		return identityService.assignPermission(roleId, permissionId);
	}
}
