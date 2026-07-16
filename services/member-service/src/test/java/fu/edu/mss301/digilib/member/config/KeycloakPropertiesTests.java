package fu.edu.mss301.digilib.member.config;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.context.ConfigurationPropertiesAutoConfiguration;
import org.springframework.boot.autoconfigure.validation.ValidationAutoConfiguration;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

class KeycloakPropertiesTests {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(
                    ConfigurationPropertiesAutoConfiguration.class,
                    ValidationAutoConfiguration.class
            ))
            .withUserConfiguration(KeycloakProperties.class)
            .withPropertyValues(
                    "keycloak.base-url=http://localhost:8180",
                    "keycloak.realm=digilib-realm",
                    "keycloak.issuer-uri=http://localhost:8180/realms/digilib-realm",
                    "keycloak.client-id=digilib-auth"
            );

    @Test
    void refusesToStartWithoutClientSecret() {
        contextRunner.run(context -> assertThat(context).hasFailed());
    }

    @Test
    void acceptsRuntimeInjectedClientSecret() {
        contextRunner
                .withPropertyValues("keycloak.client-secret=test-only-client-secret")
                .run(context -> {
                    assertThat(context).hasNotFailed();
                    assertThat(context.getBean(KeycloakProperties.class).getClientSecret())
                            .isEqualTo("test-only-client-secret");
                });
    }
}
