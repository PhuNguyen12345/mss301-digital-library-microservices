package fu.edu.mss301.digilib.identity;

import static org.assertj.core.api.Assertions.assertThat;

import fu.edu.mss301.digilib.identity.domain.entity.User;
import java.util.Arrays;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import fu.edu.mss301.digilib.identity.infrastructure.keycloak.KeycloakUserClient;

@SpringBootTest(properties = {
		"spring.datasource.url=jdbc:h2:mem:identity-context;MODE=MySQL;DB_CLOSE_DELAY=-1",
		"spring.datasource.driver-class-name=org.h2.Driver",
		"spring.jpa.hibernate.ddl-auto=create-drop",
		"eureka.client.enabled=false" })
class IdentityServiceApplicationTests {

	@MockitoBean
	KeycloakUserClient keycloakUserClient;

	@Test
	void contextLoads() {
	}

	@Test
	void userEntityDoesNotContainPasswordHash() {
		assertThat(Arrays.stream(User.class.getDeclaredFields()).map(field -> field.getName()))
				.noneMatch(fieldName -> fieldName.toLowerCase().contains("password"));
	}
}
