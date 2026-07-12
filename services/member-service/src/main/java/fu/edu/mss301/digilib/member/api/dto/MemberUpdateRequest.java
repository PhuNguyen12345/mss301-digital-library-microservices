package fu.edu.mss301.digilib.member.api.dto;

import jakarta.validation.constraints.Size;

public record MemberUpdateRequest(
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
