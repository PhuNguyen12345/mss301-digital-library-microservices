package fu.edu.mss301.digilib.identity.api.dto;

import fu.edu.mss301.digilib.identity.domain.entity.User;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record UserResponse(
		UUID userId,
		String email,
		String firstName,
		String lastName,
		String phone,
		String avatarUrl,
		String username,
		LocalDateTime createdAt,
		LocalDateTime updatedAt,
		LocalDateTime lastLogin,
		String userStatus,
		String authType,
		MemberResponse member,
		List<RoleResponse> roles,
		List<PermissionResponse> permissions) {

	public static UserResponse from(User user) {
		List<RoleResponse> roles = user.getUserRoles().stream()
				.map(userRole -> RoleResponse.from(userRole.getRole()))
				.distinct()
				.toList();
		List<PermissionResponse> permissions = user.getUserRoles().stream()
				.flatMap(userRole -> userRole.getRole().getRolePermissions().stream())
				.map(rolePermission -> PermissionResponse.from(rolePermission.getPermission()))
				.distinct()
				.toList();
		return new UserResponse(
				user.getUserId(),
				user.getEmail(),
				user.getFirstName(),
				user.getLastName(),
				user.getPhone(),
				user.getAvatarUrl(),
				user.getUsername(),
				user.getCreatedAt(),
				user.getUpdatedAt(),
				user.getLastLogin(),
				user.getUserStatus(),
				user.getAuthType(),
				MemberResponse.from(user.getMember()),
				roles,
				permissions);
	}
}
