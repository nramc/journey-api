# GitHub Copilot Instructions — Journey API

## Project Context

Java 21 + Spring Boot 4 BFF REST API for the Journeys SPA. Persists geospatial data in MongoDB using GeoJSON via the
`geojson4j` library (`io.github.nramc:geojson4j`). Multi-module Maven project — all source lives in `journey-api-web`.

---

## Code Generation Rules (Strictly Enforced by ArchUnit)

### Dependency Injection

- **Never** use `@Service`, `@Component`, or `@Repository` on your classes (except `UserDetailsManager` implementations)
- **Never** use field-level `@Autowired`
- All beans must be declared as `@Bean` methods inside `@Configuration` classes under the `config/` package
- Use constructor injection only; wiring is done in `config/ApplicationUseCaseConfig.java`,
  `config/ApplicationServiceConfig.java`, etc.
- No cyclic dependencies within `core.*` packages (ArchUnit enforces this)

### Naming & Placement

- REST controllers must be annotated with `@RestController`, reside in `web/resources/rest/`, and have class names
  ending
  with `Resource`
- All API path constants go in `web/resources/Resources.java`; custom media type constants go in `Resources.MediaType`
- New security rules (route permissions) go in `config/security/WebSecurityConfig.java` only
- Exception handling: `web/exceptions/GlobalRestExceptionHandler.java` (`@RestControllerAdvice`) maps
  `BusinessException` → 400/422, `TechnicalException` → 500, `NonTechnicalException` → 422
- OpenAPI error responses: annotate controller methods with `@RestDocCommonResponse`
  (`web/resources/rest/doc/RestDocCommonResponse.java`) to attach standard 401, 403, 400, 422, 500 responses

### Package boundaries

```
domain/    → no outward deps (no usecase/config/service/gateway/repository/web)
usecase/   → only accessed by web/resources/ and config/
gateway/   → only accessed by config/, usecase/, services/, resources/, app.health/
repository/Entity → only accessed by repository/, migration/, usecase/, resources.rest.journeys/, core.journey.security/
utils/     → no deps on web/gateway/service/repository/usecase
```

> **ArchUnit exception:** `resources.rest.users.find` may access `Repository` classes directly.

---

## Domain & Data Patterns

### GeoJSON

- Always use `geojson4j` types (e.g., `GeoJson`, `Geometry`) — never raw `Map` or `String` for geometry fields
- GeoJSON ↔ MongoDB conversion is handled by `repository/converters/JacksonBased*Converter` classes — do not add your
  own

### Journey domain

- The core aggregate is `Journey` (a Java record with Lombok `@Builder`) in `core/journey/Journey.java`
- Visibility/authorization is in `core/journey/security/JourneyAuthorizationManager.java`
- MongoDB entity is `JourneyEntity` in `repository/journey/`; converters between them are in
  `repository/journey/converter/`

### Journey update — custom media types

`PUT /rest/journey/{id}` dispatches to different `@RestController` classes based on `Content-Type`:

```
application/vnd.journey.api.basic.v1+json    → UpdateJourneyBasicDetailsResource
application/vnd.journey.api.geo.v1+json      → UpdateJourneyGeoDetailsResource
application/vnd.journey.api.images.v1+json   → UpdateJourneyImagesDetailsResource
application/vnd.journey.api.videos.v1+json   → UpdateJourneyVideosDetailsResource
application/vnd.journey.api.publish.v1+json  → PublishJourneyResource
```

Always reference constants from `Resources.MediaType` rather than hardcoding strings.

### External integrations

| Service     | Gateway class           | Config properties                                                               |
|-------------|-------------------------|---------------------------------------------------------------------------------|
| Cloudinary  | `CloudinaryGateway`     | `service.cloudinary.*` / env vars                                               |
| Telegram    | `TelegramGateway`       | `service.telegram.*` / `TELEGRAM_*`                                             |
| AI (Gemini) | Spring AI OpenAI compat | `GEMINI_API_KEY`; model `gemini-2.5-flash`; dev uses local Ollama (`qwen2.5vl`) |
| Email       | `MailService`           | `spring.mail.*` / env vars                                                      |
| WebAuthn    | `WebAuthnService`       | `app.security.webauthn.*` (rp-id, origin)                                       |

### Security / Roles

- Four roles: `GUEST_USER`, `AUTHENTICATED_USER`, `MAINTAINER`, `ADMINISTRATOR`
- Stateless JWT (RSA key pair) + HTTP Basic auth; OAuth2 resource server
- When adding a new endpoint, register its path in `Resources.java` and add an authorization rule in
  `WebSecurityConfig.securityFilterChain()`; `anyRequest().denyAll()` is the default fallback
- Journey visibility is enforced per-resource by `JourneyAuthorizationManager`
  (`core/journey/security/JourneyAuthorizationManager.java`)
- **WebAuthn (passkey/FIDO2):** configured via `WebAuthnConfig` + `WebAuthnService`; endpoints at
  `/webauthn/register`, `/webauthn/authenticate`, `/webauthn/manage`
- **TOTP MFA:** configured in `TotpConfig`; login flow — `POST /rest/login` returns an MFA challenge when MFA is
  enabled, completed with `POST /rest/mfa`

---

## Testing

- Tests use **Testcontainers** for MongoDB — no local DB needed
- Spring Cloud Contract stubs live in `src/test/resources/contracts/`; base class is `JourneyApiContractBase`
- Architecture rules are in `ApplicationArchitectureTest` — run `./mvnw verify` to check; violations fail the build
- Checkstyle config: `config/checkstyle/journey_checks.xml`; suppressions:
  `config/checkstyle/checkstyle-suppressions.xml`

---

## Environment / Config

| Profile     | JWT keys                          | Mail                     | AI model                                       |
|-------------|-----------------------------------|--------------------------|------------------------------------------------|
| `dev`       | classpath (`app.key` / `app.pub`) | Mailpit `localhost:1025` | Local Ollama (`qwen2.5vl`)                     |
| `prod`      | `/etc/secrets/jwt-private.key`    | SMTP via env vars        | Gemini `gemini-2.5-flash` via `GEMINI_API_KEY` |
| `workspace` | same as `dev`                     | same as `dev`            | disables Docker Compose auto-start (use in CI) |

Key env vars (prod): `GEMINI_API_KEY`, `NOREPLY_EMAIL`, `NOREPLY_EMAIL_PWD`, `CLOUDINARY_API_KEY`,
`CLOUDINARY_API_SECRET`, `CLOUDINARY_CLOUD_NAME`, `TELEGRAM_BOT_TOKEN`, `TELEGRAM_CHANNEL_ID`.

---

## Build Commands

```bash
docker compose up -d                              # start MongoDB, Mongo Express, Mailpit
./mvnw spring-boot:run -pl journey-api-web -Dspring-boot.run.profiles=dev  # run locally
./mvnw verify                                     # build + checkstyle + all tests
./mvnw verify -P coverage                         # with JaCoCo report
./mvnw verify -P open-rewrite                     # apply OpenRewrite recipes first
```

- Dev actuator runs on port **8081** (separate from the main app port)
- Mongo Express UI: http://localhost:9090 | Mailpit UI: http://localhost:8025
- Use `-Dspring-boot.run.profiles=dev,workspace` when infrastructure is already running externally (CI)

