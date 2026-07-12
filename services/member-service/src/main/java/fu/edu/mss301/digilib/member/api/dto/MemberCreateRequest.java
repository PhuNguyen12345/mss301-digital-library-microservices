package fu.edu.mss301.digilib.member.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record MemberCreateRequest(
        @NotBlank
        String id,

        @NotBlank
        @Email
        String email,

        @Size(max = 100)
        String firstName,

        @Size(max = 100)
        String lastName,

        @Size(max = 30)
        String phone,

        @Size(max = 512)
        String avatarKey
) {
}
