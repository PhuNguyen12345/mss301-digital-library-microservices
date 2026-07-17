package fu.edu.mss301.digilib.gateway.security;

import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jwt.Jwt;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class KeycloakRealmRoleConverterTests {

    private final KeycloakRealmRoleConverter converter = new KeycloakRealmRoleConverter();

    @Test
    void mapsScopesAndKeycloakRealmRoles() {
        Jwt jwt = new Jwt(
                "test-token",
                Instant.now(),
                Instant.now().plusSeconds(60),
                Map.of("alg", "none"),
                Map.of(
                        "sub", "member-1",
                        "scope", "catalog:read",
                        "realm_access", Map.of("roles", List.of("member", "LIBRARIAN"))));

        assertThat(converter.convert(jwt))
                .extracting("authority")
                .contains("SCOPE_catalog:read", "ROLE_MEMBER", "ROLE_LIBRARIAN");
    }

    @Test
    void handlesTokenWithoutRealmRoles() {
        Jwt jwt = new Jwt(
                "test-token",
                Instant.now(),
                Instant.now().plusSeconds(60),
                Map.of("alg", "none"),
                Map.of("sub", "member-1"));

        assertThat(converter.convert(jwt)).isEmpty();
    }
}
