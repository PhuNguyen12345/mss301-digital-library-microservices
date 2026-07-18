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
