package fu.edu.mss301.digilib.identity.api.dto;

import jakarta.validation.constraints.NotBlank;

public record PermissionCreateRequest(@NotBlank String permissionName, @NotBlank String resource,
		@NotBlank String action) {
}
