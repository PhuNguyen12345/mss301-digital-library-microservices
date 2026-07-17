package fu.edu.mss301.digilib.gateway;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
		"spring.cloud.config.enabled=false",
		"spring.cloud.discovery.enabled=false",
		"eureka.client.enabled=false",
		"spring.security.oauth2.resourceserver.jwt.issuer-uri=http://localhost:8180/realms/digilib-realm"
})
class DigilibApiGatewayApplicationTests {

	@Test
	void contextLoads() {
	}

}
