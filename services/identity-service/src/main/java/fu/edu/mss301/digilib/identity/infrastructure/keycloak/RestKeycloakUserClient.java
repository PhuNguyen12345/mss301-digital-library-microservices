package fu.edu.mss301.digilib.identity.infrastructure.keycloak;

import fu.edu.mss301.digilib.identity.api.dto.UserCreateRequest;
import java.net.URI;
import java.util.List;
import java.util.Map;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;

@Component
public class RestKeycloakUserClient implements KeycloakUserClient {

	private final RestClient restClient;
	private final KeycloakProperties properties;

	public RestKeycloakUserClient(RestClient keycloakRestClient, KeycloakProperties properties) {
		this.restClient = keycloakRestClient;
		this.properties = properties;
	}

	@Override
	public String createUser(UserCreateRequest request) {
		String token = adminToken();
		Map<String, Object> payload = Map.of(
				"username", request.username(),
				"email", request.email(),
				"firstName", request.firstName(),
				"lastName", request.lastName(),
				"enabled", properties.isDefaultEnabled(),
				"credentials", List.of(Map.of("type", "password", "value", request.password(), "temporary", false)));

		ResponseEntity<Void> response = restClient.post()
				.uri("/admin/realms/{realm}/users", properties.getRealm())
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + token)
				.body(payload)
				.retrieve()
				.toBodilessEntity();

		URI location = response.getHeaders().getLocation();
		if (location == null) {
			throw new IllegalStateException("Keycloak did not return created user location");
		}
		String path = location.getPath();
		return path.substring(path.lastIndexOf('/') + 1);
	}

	@Override
	public void deleteUser(String keycloakUserId) {
		if (!StringUtils.hasText(keycloakUserId)) {
			return;
		}
		restClient.delete()
				.uri("/admin/realms/{realm}/users/{userId}", properties.getRealm(), keycloakUserId)
				.header("Authorization", "Bearer " + adminToken())
				.retrieve()
				.toBodilessEntity();
	}

	@SuppressWarnings("unchecked")
	private String adminToken() {
		MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
		form.add("grant_type", "client_credentials");
		form.add("client_id", properties.getClientId());
		form.add("client_secret", properties.getClientSecret());
		Map<String, Object> body = restClient.post()
				.uri("/realms/{realm}/protocol/openid-connect/token", properties.getRealm())
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.body(form)
				.retrieve()
				.body(Map.class);
		if (body == null || !body.containsKey("access_token")) {
			throw new IllegalStateException("Keycloak token response did not include access_token");
		}
		return String.valueOf(body.get("access_token"));
	}
}
