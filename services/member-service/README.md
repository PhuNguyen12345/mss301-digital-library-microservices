# Member Service

## Overview
The member service is the profile and user-management boundary for the Digital Library backend.

It is designed to own the member profile domain, while authentication itself is expected to be handled by an external identity provider or a dedicated identity service. In the current codebase, the service already accepts and validates JWTs, then uses the token claims to create or fetch the current user's profile.

## What this service is supposed to do
Target responsibilities for this service:

- Serve authenticated user's profile data.
- Auto-create a member profile when a new authenticated user appears.
- Support profile lookup by member id.
- Support profile update flows.
- Support admin-style member listing.
- Support activate and deactivate user state.
- Integrate cleanly with the identity layer for login and logout flows, rather than implementing username/password auth locally.

## Current implementation status

### Implemented
- `GET /api/v1/members/me` returns the current authenticated user's profile and auto-provisions a record if none exists.
- `GET /api/v1/members/{memberId}` fetches a profile by id.
- `PATCH /api/v1/members/me` updates editable profile fields for the authenticated user.
- JWT-based resource-server security is configured.
- Keycloak-style claims are already being read from the token:
  - `sub` for the stable user id
  - `email`
  - `given_name`
  - `family_name`
- Reactive R2DBC persistence is wired through `MemberPersistenceAdapter`.
- API responses are mapped through `MemberResponse` instead of exposing the persistence entity directly.

### Not implemented yet
- Login endpoint.
- Logout endpoint.
- Register endpoint as a dedicated user-registration API.
- List all users endpoint.
- Activate/deactivate user endpoint.
- Search/filter support.
- Audit logging.

## Technical stack
- Java 21
- Spring Boot WebFlux
- Spring Security resource server with JWT
- Spring Data R2DBC
- PostgreSQL via R2DBC driver
- Eureka client registration
- Spring Cloud OpenFeign for declarative service-to-service clients
- Lombok
- Maven multi-module build

## Module location
- Module root: `services/member-service`
- Main application: `src/main/java/fu/edu/mss301/digilib/member/MemberServiceApplication.java`
- Security config: `src/main/java/fu/edu/mss301/digilib/member/config/SecurityConfig.java`
- Profile controller: `src/main/java/fu/edu/mss301/digilib/member/api/controller/MemberProfileController.java`
- Domain service: `src/main/java/fu/edu/mss301/digilib/member/domain/service/MemberProfileService.java`

## Current API surface

### `GET /api/v1/members/me`
Returns the current authenticated member profile.

Behavior:
- Reads JWT claims.
- Uses `sub` as the member id.
- Creates a new profile if one does not exist yet.

### `GET /api/v1/members/{memberId}`
Fetches a profile by id for internal consumption.

## Domain model snapshot
The main domain entity is `MemberProfile`.

Important fields already present:

- `id`
- `email`
- `firstName`
- `lastName`
- `phone`
- `memberType`
- `memberCode`
- `borrowingLimit`
- `loanPeriodDays`
- `outstandingBalance`
- `avatarKey`
- `createdAt`
- `updatedAt`

## Main gaps in the current code

### 1. No list/admin endpoints
The controller currently exposes authenticated self-profile operations and id lookup only.

### 2. No activation state
There is no active/inactive flag in the entity, so deactivate/reactivate cannot be represented yet.

### 3. Auth lifecycle is external
This service is not a password-based auth server.

That means login/logout should either:
- live in the identity service, or
- be delegated to an external identity provider such as Keycloak.

### 4. No database migration/schema ownership
The service maps to the `member_profiles` table, but there is no migration file in this module that creates or evolves that table.

Impact:
- Fresh environments need manual schema setup.
- Adding activation state or admin filters should be backed by an explicit migration.

## Recommended implementation plan

### Phase 1: Make the current profile flow real - done
- Implemented `MemberPersistenceAdapter` using `MemberR2dbcRepository`.
- Added repository lookup by email.
- Finished the `MemberProfileService` path so `GET /me` persists and returns a profile.
- Added request/response DTO mapping.
- Aligned this module with the parent Spring Boot version instead of overriding it to Boot 4.
- Removed the servlet MVC starter so the module remains WebFlux-only.

### Phase 2: Add profile update support - done
- Added `PATCH /api/v1/members/me` for the authenticated user's profile.
- Allow safe updates only for editable fields such as:
  - `firstName`
  - `lastName`
  - `phone`
  - `avatarKey`
- Keep identity fields such as `id`, `email`, and `memberCode` immutable.
- Update `updatedAt` on each save.

### Phase 3: Add admin member management
- Add a paged `GET /api/v1/members` endpoint.
- Add search/filter support for email, name, type, and status.
- Add `activate` and `deactivate` operations.
- Protect these endpoints with role-based access control.

### Phase 4: Decide auth boundary clearly
- Keep this service focused on profile data and account state.
- Move login/logout/register credential handling to the identity service or external IdP.
- Define how identity events create member profiles, for example:
  - first login profile bootstrap
  - registration webhook/event
  - message-driven user provisioning

### Phase 5: Harden quality and delivery
- Add unit tests for service logic.
- Add repository/integration tests for R2DBC persistence.
- Add validation for request DTOs.
- Add OpenAPI or documented contracts for frontend and other services.
- Add audit logging for admin state changes.

## Suggested target API map

- `GET /api/v1/members/me` - current authenticated profile
- `GET /api/v1/members/{memberId}` - profile lookup by id
- `PATCH /api/v1/members/me` - update own profile
- `GET /api/v1/members` - list members for admins
- `PATCH /api/v1/members/{memberId}/activate` - activate a member
- `PATCH /api/v1/members/{memberId}/deactivate` - deactivate a member

## Notes for future work
- The service is currently built as a WebFlux application, so any new code should stay reactive end-to-end.
- Avoid reintroducing `spring-boot-starter-webmvc`; this module uses the reactive stack.
- OpenFeign is available for declarative service-to-service calls, but its clients are blocking; do not call Feign clients directly from reactive request paths without isolating the blocking work.
- The current security configuration is token-based and already aligned with a centralized identity provider.

## Conclusion
This module is now a working starting point for an authenticated member profile service, but it is not yet a complete user-management service. The next high-value step is to add schema migrations, then implement admin list/status endpoints with role-based authorization while keeping authentication responsibility outside this module.
