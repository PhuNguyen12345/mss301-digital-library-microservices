package fu.edu.mss301.digilib.identity.api.dto;

import jakarta.validation.constraints.NotBlank;

public record RoleCreateRequest(@NotBlank String roleName, String description) {
}
