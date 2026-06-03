package fu.edu.mss301.digilib.identity.infrastructure.keycloak;

import fu.edu.mss301.digilib.identity.api.dto.UserCreateRequest;

public interface KeycloakUserClient {

	String createUser(UserCreateRequest request);

	void deleteUser(String keycloakUserId);
}
