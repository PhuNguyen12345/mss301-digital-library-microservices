# Security bootstrap: Keycloak and internal service exposure

## Rotate the leaked Keycloak client secret

The historical `digilib-auth` client secret must be considered compromised because it was committed to source control. Removing it from the current tree does not revoke it.

1. Sign in to the Keycloak Admin Console for the target environment.
2. Select realm `digilib-realm`, then open **Clients > digilib-auth > Credentials**.
3. Regenerate the client secret. Regeneration invalidates the previous value.
4. Copy `.env.example` to `.env` and set `KEYCLOAK_CLIENT_SECRET` to the regenerated value. In production, inject this value from the deployment secret store instead of a file.
5. Set `KEYCLOAK_BASE_URL` and `KEYCLOAK_ISSUER_URI` for that same Keycloak deployment. The issuer must exactly equal the `iss` claim in issued access tokens.
6. Restart Member Service and verify register, login, token refresh and logout.

Do not paste the new secret into YAML, documentation, chat logs, CI variables printed by jobs, or a pull-request description.

## Local startup

From the repository root:

```powershell
Copy-Item .env.example .env
# Edit .env and replace every placeholder secret.
docker compose --env-file .env -f docker-compose.infra.yml up -d
.\start-services.bat
```

Member Service validates its confidential-client properties during startup and refuses to start when the client secret is blank or unresolved.

`INTERNAL_API_KEY` is a different secret from the Keycloak client secret. Generate
at least 32 random characters and put the same value in the runtime environment of
both Member Service and Loan Service. For local development, generate a 64-character
hex value in PowerShell and paste it into `.env` without committing the file:

```powershell
[Convert]::ToHexString([Security.Cryptography.RandomNumberGenerator]::GetBytes(32)).ToLowerInvariant()
```

```properties
INTERNAL_API_KEY=<paste-the-generated-value-here>
```

Member Service rejects missing, placeholder, or shorter internal keys during startup.
Requests to `/api/v1/members/internal/**` must carry this value in the
`X-Internal-Api-Key` header. API Gateway deliberately returns `404` for that path,
so it remains available only to trusted service-to-service callers on the internal
network.

## Direct-access boundary

Business services bind to `127.0.0.1` by default, while API Gateway remains the public entry point. This blocks access to business-service ports from other hosts when services run as local Java processes.

Config Server and Eureka also bind to host loopback by default so configuration data and the service registry are not exposed as public endpoints.

For a container deployment:

- Set `INTERNAL_SERVICE_BIND_ADDRESS=0.0.0.0`.
- Set `INTERNAL_SERVICE_HOST` to the service's internal DNS name, or configure the equivalent Eureka metadata per service.
- Use `expose` for business-service ports when documentation is useful, but never add host `ports` mappings for them.
- Publish only API Gateway's application port.
- Keep Keycloak admin and database endpoints private; the local Compose file binds them to host loopback only.

Network isolation makes Gateway the first external enforcement point. Sensitive business services should still validate end-user or service credentials as defense in depth.

## Required Keycloak realm configuration

The repository does **not** ship a realm-export JSON. The realm must be configured manually in the Keycloak Admin Console before the application is useful. The codebase references the following realm roles by name (case-insensitive in checks); they must all exist in `digilib-realm`:

- `admin` — granted manually by the operator; gates `/actuator/**` at the gateway.
- `librarian` — granted manually by the operator (never self-assigned); gates staff-only endpoints (`/api/notifications/jobs/**`, loan bookkeeping, member status changes).
- `student` — self-assigned via `PATCH /api/v1/members/me/role` during onboarding.
- `lecturer` — self-assigned via `PATCH /api/v1/members/me/role` during onboarding.

### Onboarding flow

User registration (via Keycloak Admin REST API in `AuthService.register`) deliberately does **not** assign any realm role. New users show up with no onboarding role. The frontend is expected to send them to the role-choice screen, where they call `PATCH /api/v1/members/me/role` with `{"role": "student"}` or `{"role": "lecturer"}`. The member-service:

1. Lists current realm roles; removes any prior `student`/`lecturer` (so a user only ever holds one).
2. Assigns the new role via the Keycloak admin role-mapping API.
3. Updates `member_profiles.member_type` to mirror the choice (`STUDENT` or `LECTURER`), and rolls back the Keycloak role assignment if the profile update fails.

The gateway's `OnboardingRequiredFilter` blocks any authenticated request that doesn't carry a `student`/`lecturer`/`librarian`/`admin` role with `403 ONBOARDING_REQUIRED`. Whitelisted paths (`/actuator/**`, `/api/v1/auth/**`, `GET|PATCH /api/v1/members/me`, `PATCH /api/v1/members/me/role`) are still reachable so the user can read their profile and complete onboarding.

### Realm default-roles

In the Keycloak Admin Console under **Realm settings > User registration > Default roles**, leave the default-roles set as `offline_access` (or empty) — do **not** include `student`/`lecturer`/`librarian`. The whole point of onboarding is that users self-select, and a default role would defeat that.

### Client configuration

Client `digilib-auth` (`KEYCLOAK_CLIENT_ID`) must be marked **confidential** with these grants enabled in the Admin Console:

- **Service Accounts Enabled** — used by member-service for the `client_credentials` admin token (creates users, assigns roles, sends verification email).
- **Direct Access Grants Enabled** — used by `POST /api/v1/auth/login` (ROPC).
- **Authorization Code** with PKCE — used by the frontend via `POST /api/v1/auth/oauth2/exchange`.

After regenerating the client secret per the section above, paste it into `.env` as `KEYCLOAK_CLIENT_SECRET` (never commit it).

### Service account realm-management roles (REQUIRED for onboarding)

The `digilib-auth` service account (the access token obtained via `client_credentials`) calls the Keycloak Admin REST API to manage users and realm roles. Without the right admin permissions, `register` works but the onboarding endpoint (`PATCH /api/v1/members/me/role`) returns `503 SERVICE_ACCOUNT_UNAUTHORIZED` with the request body:

```
GET  /admin/realms/digilib-realm/roles/{roleName}      → requires view-realm
POST /admin/realms/digilib-realm/users/{id}/role-mappings/realm → requires manage-users
GET  /admin/realms/digilib-realm/users/{id}/role-mappings/realm → requires view-users
```

Grant these roles in the Keycloak Admin Console:

1. Realm `digilib-realm` → **Clients → digilib-auth → Service Accounts Roles**.
2. Under **Client roles → realm-management**, click **Assign role** and add:
   - `view-realm` (read realm-role metadata — was the cause of the original 403 in the onboarding flow).
   - `manage-users` (assign / remove role mappings for a user; also needed by `createUser`, `setPassword`, `sendVerificationEmail`).
   - `view-users` (list a user's realm role mappings).
3. **Save**.  Rotate (re-fetch) the service-account token by restarting member-service, or wait for the existing token to expire (default ~5 minutes).

If `register` worked but `PATCH /me/role` returns `403 FORBIDDEN from GET .../admin/realms/digilib-realm/roles/student`, the missing role is almost always `view-realm`. If `register` itself fails with a similar 403, check that `manage-users` is granted.

### Seeded librarian

The `member-service` Flyway migration `V3__seed_members.sql` seeds several profiles for development. The seeded librarian profile (`staff.sarah@example.com`) only has the `LIBRARIAN` *profile tier* in the database — the corresponding `librarian` **realm role** must still be granted to that Keycloak user manually for `@PreAuthorize("hasAnyRole('ADMIN','LIBRARIAN')")` checks to pass.

## Google Identity Provider checklist

Google login is configured entirely inside the Keycloak Admin Console — there is no code path, no `IdentityProvider` registration, and no `oauth2-client` config in the repository. Verify each environment before relying on it:

1. In the Keycloak Admin Console for `digilib-realm`, open **Identity Providers > Add provider > Google**.
2. Provide the Google Client ID / Client Secret from the Google Cloud Console (OAuth 2.0 Client ID of type *Web application*).
3. The Authorized redirect URI on the Google side must be `https://<keycloak-base>/realms/digilib-realm/broker/google/endpoint`.
4. Default scopes: `openid email profile`.
5. Frontend integration: append `kc_idp_hint=google` to the OIDC authorization URL (see `docs/member-service/05-frontend-integration.md`).
6. Verification step: complete a Google sign-in once and confirm `GET /api/v1/members/me` returns a profile. JIT profile creation in `MemberProfileService.registerOrFetchProfile` should populate a `READER`-tier profile with no onboarding role assigned; the user must then go through the onboarding screen just like an email-registered user.

## Internal API key enforcement

`INTERNAL_API_KEY` is shared by `member-service`, `loan-service`, and `notification-service`. Each service validates it for the internal service-to-service paths listed below. All comparisons are constant-time (`MessageDigest.isEqual`).

| Service | Protected path | Enforcement |
| --- | --- | --- |
| `member-service` | `GET /api/v1/members/internal/{memberId}` | `InternalApiKeyWebFilter` (highest-precedence WebFilter, before Spring Security). Startup rejects placeholder/short keys. |
| `loan-service` | `GET /api/v1/loans/internal/due-soon`, `GET /api/v1/loans/internal/overdue` | `InternalLoanController#verify` on every request. Constant-time comparison. |
| `notification-service` | `POST /api/notifications`, `POST /api/notifications/return-confirmation` | `InternalApiKeyFilter` (servlet `OncePerRequestFilter`). Startup rejects placeholder/short keys. |

The gateway's `InternalEndpointBlockFilter` additionally returns `404` for `/api/v1/members/internal/**`, `POST /api/notifications`, and `POST /api/notifications/return-confirmation` so external callers cannot probe them. The service-level filters are defense-in-depth for cases where a service is reachable directly on the container network.

## Bind address and actuator exposure

Business services' `application.yaml` defaults `server.address` to `127.0.0.1` (loopback). Local Java processes are reachable only through the API Gateway; container deployments must set `INTERNAL_SERVICE_BIND_ADDRESS=0.0.0.0` and **must not** publish business-service host ports (use Docker `expose`, not `ports`). Only the API Gateway's port (8080) should be published.

`/actuator/**` is `permitAll` on several business services for liveness/readiness checks. If you publish a business-service port to the host by mistake, every actuator endpoint (env, heapdump, etc.) becomes reachable. The gateway gates `/actuator/**` with `hasRole("ADMIN")` (except `health` and `info`) so external exposure is bounded, but business services should still remain on the internal network.

## Backend role-gating matrix

Defense in depth: the API gateway enforces role checks first; downstream services re-check the same rules locally so a bypassed/misconfigured gateway still cannot let an unauthorized call through. Mirrors the rules defined in `GatewaySecurityConfig.java`, `catalog-service/.../config/SecurityConfig.java`, `loan-service/.../config/SecurityConfig.java`, `notification-service/.../config/SecurityConfig.java`, and `member-service` `@PreAuthorize` annotations.

| Path pattern | Verb | Required role(s) | Enforced at |
| :--- | :--- | :--- | :--- |
| `/actuator/**` (everything except `health`/`info`) | any | `admin` | gateway + each service |
| `/api/v1/auth/register`, `/api/v1/auth/login`, `/api/v1/auth/oauth2/exchange`, `/api/v1/auth/forgot-password` | POST | public (no JWT) | gateway |
| `/api/v1/members/internal/**` | GET | internal-only (gateway 404s; service checks `X-Internal-Api-Key`) | gateway + member-service |
| `/api/v1/members` (list) | GET | `admin` or `librarian` | member-service (`@PreAuthorize`) |
| `/api/v1/members/{id}/status` | PUT | `admin` or `librarian` | member-service (`@PreAuthorize`) |
| `/api/v1/members/{id}` | GET | `admin`/`librarian` **or** self (`#memberId == jwt.subject`) | member-service (`@PreAuthorize` with SpEL) |
| `/api/v1/members/me`, `/api/v1/members/me/role` | GET/PATCH | any authenticated (whitelisted by `OnboardingRequiredFilter`) | gateway + member-service |
| `/api/v1/loans/internal/**` | GET | internal-only (`X-Internal-Api-Key`) | loan-service controller check |
| `/api/v1/borrow-requests`, `/api/v1/borrow-requests/*/approve|reject`, `/api/v1/rent-books`, `/api/v1/loans/return`, `/api/v1/loans/*/lost`, `/api/v1/loans` (list) | various | `admin` or `librarian` | loan-service `SecurityConfig` |
| `/api/notifications`, `/api/notifications/return-confirmation` | POST | internal-only (`InternalApiKeyFilter`) | gateway 404 + notification-service filter |
| `/api/notifications/jobs/**`, `/api/notifications` (search) | any | `admin` or `librarian` | gateway + notification-service `SecurityConfig` |
| `/api/catalog/**` | GET | any authenticated | gateway + catalog-service `SecurityConfig` |
| `/api/catalog/**` (mutating verbs: POST/PUT/PATCH/DELETE) | non-GET | `admin` or `librarian` | gateway + catalog-service `SecurityConfig` |
| `/api/fines/payments/sepay/webhook` | POST | public (HMAC-verified by `SepayWebhookVerifier`) | gateway |

**Adding a new admin-only path** under an existing prefix (e.g. a new `/api/catalog/reports/**` route) requires no gateway change — the existing `anyExchange` rule for the prefix covers it. **Adding a new prefix** (e.g. `/api/reports/**`) requires an explicit rule in `GatewaySecurityConfig` and the new service's own `SecurityConfig` to be safe. `student` and `lecturer` are intentionally never listed in any right-hand column — they're read-only roles for the catalog and their own profile.
