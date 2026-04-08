# SportLink - Current Version

## Project Structure

- `frontend/` exists and is currently empty (reserved for UI implementation).
- `backend/` contains the Spring Boot backend and all MVP API logic.

## Backend Setup

- Backend initialized from Spring Initializr and moved under `backend/`.
- Java and Maven wrapper configured (`mvnw`, `mvnw.cmd`).
- Active dependencies in `backend/pom.xml`:
  - `spring-boot-starter-webmvc`
  - `spring-boot-starter-data-jpa`
  - `spring-boot-starter-validation`
  - `org.postgresql:postgresql` (runtime)
  - Spring Boot test starters

## Database Status

- The project was initially configured for SQLite and later migrated to PostgreSQL.
- Current DB config is PostgreSQL in `backend/src/main/resources/application.properties`.
- Datasource supports environment overrides:
  - `DB_URL` (default `jdbc:postgresql://localhost:5432/sportlink`)
  - `DB_USER` (default `postgres`)
  - `DB_PASSWORD` (default `postgres`)
  - `DDL_AUTO` (default `update`)
- PostgreSQL dialect is configured.

## Implemented MVP Features

### Authentication

- `POST /api/auth/register`
  - Creates user account directly in DB (no email verification flow).
- `POST /api/auth/login`
  - Authenticates user by username and password.

### Lobby Management

- `POST /api/lobbies`
- `POST /api/lobbies/create` (alias route)
- `GET /api/lobbies/active`
- `POST /api/lobbies/{lobbyId}/join`
- `POST /api/lobbies/{lobbyId}/leave`

### System Routes

- `GET /` returns backend info and route hints.
- `GET /health` returns service status.

## Backend Architecture (Layered)

- **Entity layer**
  - `UserAccount` -> table `users`
  - `Lobby` -> table `lobbies`
  - many-to-many participant relation through `user_lobby`
- **Repository layer**
  - `UserAccountRepository`
  - `LobbyRepository`
- **Service layer**
  - `AuthService`
  - `LobbyService`
- **Controller layer**
  - `AuthController`
  - `LobbyController`
  - `HomeController`
- **Exception layer**
  - Global REST error handling with consistent JSON error responses.

## Validation and Business Rules

- DTO-level validation added with Bean Validation (`@NotBlank`, `@NotNull`, `@Positive`, `@Future`, `@Size`, `@Pattern`).
- Input validation enabled in controllers via `@Valid`.
- Business constraints implemented in services:
  - duplicate username blocked
  - lobby capacity enforced
  - join blocked if user already joined
  - leave blocked if user not in lobby
  - lobby date/time must be in the future

## Documentation and Testing Artifacts

- Postman collection added:
  - `backend/postman/SportLink-Phase2.postman_collection.json`
  - Covers health, register, login, create lobby, list active, join, leave
- Additional backend phase notes were prepared during implementation in chat and code updates.

## Verification Status

- Maven validation and tests were run successfully after major changes.
- Lint check on modified files showed no linter errors.
- API flows were manually testable with `curl`/Postman.

## Current Notes

- `backend/sportlink.db` file still exists from earlier SQLite phase, but the active configuration is PostgreSQL.
- Next logical step is frontend implementation and integration with current REST API contract.
