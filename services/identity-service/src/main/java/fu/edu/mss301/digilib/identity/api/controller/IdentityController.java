package fu.edu.mss301.digilib.identity.api.controller;

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
