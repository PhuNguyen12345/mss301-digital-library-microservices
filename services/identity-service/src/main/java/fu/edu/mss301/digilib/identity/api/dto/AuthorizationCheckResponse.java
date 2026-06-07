package fu.edu.mss301.digilib.identity.api.dto;

import java.util.UUID;

public record AuthorizationCheckResponse(UUID userId, String resource, String action, boolean granted) {
}
