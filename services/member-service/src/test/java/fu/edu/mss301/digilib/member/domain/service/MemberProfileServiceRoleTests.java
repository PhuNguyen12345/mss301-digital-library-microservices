package fu.edu.mss301.digilib.member.domain.service;

import fu.edu.mss301.digilib.member.api.error.ApiException;
import fu.edu.mss301.digilib.member.domain.entity.MemberProfile;
import fu.edu.mss301.digilib.member.domain.repository.MemberProfileRepository;
import fu.edu.mss301.digilib.member.infrastructure.keycloak.KeycloakAdminClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MemberProfileServiceRoleTests {

    private static final String KEYCLOAK_ID = "keycloak-user-1";

    @Mock
    private MemberProfileRepository repository;
    @Mock
    private KeycloakAdminClient keycloakClient;

    private MemberProfileService service;

    @BeforeEach
    void setUp() {
        service = new MemberProfileService(repository, keycloakClient);
    }

    @Test
    void rejectsInvalidRoleName() {
        StepVerifier.create(service.assignUserRole(KEYCLOAK_ID, "librarian"))
                .expectErrorSatisfies(error -> {
                    assertThat(error).isInstanceOf(ApiException.class);
                    ApiException apiException = (ApiException) error;
                    assertThat(apiException.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
                    assertThat(apiException.getCode()).isEqualTo("INVALID_ROLE");
                })
                .verify();

        verify(keycloakClient, never()).listUserRealmRoles(any());
        verify(keycloakClient, never()).assignRealmRole(any(), any());
    }

    @Test
    void assignsStudentRoleWhenUserHasNone() {
        when(keycloakClient.listUserRealmRoles(KEYCLOAK_ID)).thenReturn(Mono.just(List.of()));
        when(keycloakClient.assignRealmRole(KEYCLOAK_ID, "student")).thenReturn(Mono.empty());

        MemberProfile existing = MemberProfile.builder()
                .id(KEYCLOAK_ID)
                .memberType("READER")
                .createdAt(Instant.parse("2026-01-01T00:00:00Z"))
                .build();
        when(repository.findById(KEYCLOAK_ID)).thenReturn(Mono.just(existing));
        when(repository.save(any(MemberProfile.class))).thenAnswer(inv -> Mono.just(inv.getArgument(0)));

        StepVerifier.create(service.assignUserRole(KEYCLOAK_ID, "student"))
                .assertNext(profile -> assertThat(profile.getMemberType()).isEqualTo("STUDENT"))
                .verifyComplete();

        verify(keycloakClient).assignRealmRole(KEYCLOAK_ID, "student");
        verify(keycloakClient, never()).removeRealmRole(any(), any());
    }

    @Test
    void swapsLecturerForStudentAndRemovesPriorRole() {
        when(keycloakClient.listUserRealmRoles(KEYCLOAK_ID)).thenReturn(Mono.just(List.of("lecturer", "offline_access")));
        when(keycloakClient.removeRealmRole(KEYCLOAK_ID, "lecturer")).thenReturn(Mono.empty());
        when(keycloakClient.assignRealmRole(KEYCLOAK_ID, "student")).thenReturn(Mono.empty());

        MemberProfile existing = MemberProfile.builder()
                .id(KEYCLOAK_ID)
                .memberType("LECTURER")
                .createdAt(Instant.parse("2026-01-01T00:00:00Z"))
                .build();
        when(repository.findById(KEYCLOAK_ID)).thenReturn(Mono.just(existing));
        when(repository.save(any(MemberProfile.class))).thenAnswer(inv -> Mono.just(inv.getArgument(0)));

        StepVerifier.create(service.assignUserRole(KEYCLOAK_ID, "STUDENT"))
                .assertNext(profile -> assertThat(profile.getMemberType()).isEqualTo("STUDENT"))
                .verifyComplete();

        verify(keycloakClient).removeRealmRole(KEYCLOAK_ID, "lecturer");
        verify(keycloakClient).assignRealmRole(KEYCLOAK_ID, "student");
    }

    @Test
    void rollsBackKeycloakRoleWhenProfileUpdateFails() {
        when(keycloakClient.listUserRealmRoles(KEYCLOAK_ID)).thenReturn(Mono.just(List.of()));
        when(keycloakClient.assignRealmRole(KEYCLOAK_ID, "student")).thenReturn(Mono.empty());
        when(repository.findById(KEYCLOAK_ID)).thenReturn(Mono.empty()); // profile missing -> update fails
        when(keycloakClient.removeRealmRole(KEYCLOAK_ID, "student")).thenReturn(Mono.empty());

        StepVerifier.create(service.assignUserRole(KEYCLOAK_ID, "student"))
                .expectErrorSatisfies(error -> {
                    assertThat(error).isInstanceOf(ApiException.class);
                    ApiException apiException = (ApiException) error;
                    assertThat(apiException.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
                    assertThat(apiException.getCode()).isEqualTo("MEMBER_PROFILE_NOT_FOUND");
                })
                .verify();

        verify(keycloakClient).assignRealmRole(KEYCLOAK_ID, "student");
        verify(keycloakClient).removeRealmRole(KEYCLOAK_ID, "student");
    }

    @Test
    void keepsOriginalErrorWhenRollbackAlsoFails() {
        when(keycloakClient.listUserRealmRoles(KEYCLOAK_ID)).thenReturn(Mono.just(List.of()));
        when(keycloakClient.assignRealmRole(KEYCLOAK_ID, "student")).thenReturn(Mono.empty());
        when(repository.findById(KEYCLOAK_ID)).thenReturn(Mono.error(new IllegalStateException("database unavailable")));
        when(keycloakClient.removeRealmRole(KEYCLOAK_ID, "student"))
                .thenReturn(Mono.error(new IllegalStateException("Keycloak unavailable")));

        StepVerifier.create(service.assignUserRole(KEYCLOAK_ID, "student"))
                .expectError(IllegalStateException.class)
                .verify();

        verify(keycloakClient, times(1)).assignRealmRole(KEYCLOAK_ID, "student");
        verify(keycloakClient, times(1)).removeRealmRole(KEYCLOAK_ID, "student");
    }
}