# AGENTS.md — Journey API

## Project Overview

Java 21 + Spring Boot 4 **BFF (Backend For Frontend)** REST API for the [Journeys SPA](https://journey.codewithram.dev).
Persists geospatial journey data in MongoDB Atlas using GeoJSON (`geojson4j` library). Deployed via Docker to Render.


**Multi-module Maven project:**

- `journey-api-web` — the runnable Spring Boot application (all source code lives here)
- `journey-api-tests` — separate integration/contract test module with Allure results

**Spring Modulith:**

- The project uses [Spring Modulith](https://docs.spring.io/spring-modulith/docs/current/reference/html/) for modularity and event publication. See `infrastructure/event/EventRepublisher.java` and `infrastructure/config/InfrastructureConfig.java` for event republishing and configuration.

---

## Developer Workflows

### Local development

```bash
# Run app with dev profile — Spring Boot auto-starts MongoDB, Mongo Express & Mailpit via Docker Compose
./mvnw spring-boot:run -pl journey-api-web -Dspring-boot.run.profiles=dev
```

- Dev profile enables HTTPS (`keystore/localhost.p12`), JWT keys from classpath (`app.key`/`app.pub`), and Mailpit at
  `localhost:1025`
- Dev actuator runs on port **8081** (separate from the app port)
- Mongo Express UI: http://localhost:9090
- Mailpit UI: http://localhost:8025
- **`workspace` profile** (`application-workspace.yml`): disables Spring Docker Compose integration — use this in CI or
  when the infrastructure is already running externally

### Build & test

```bash
./mvnw verify                        # build + checkstyle + tests
./mvnw verify -P coverage            # with JaCoCo coverage report
./mvnw verify -P open-rewrite        # apply OpenRewrite recipes then verify
```

Checkstyle runs at `verify` phase using `config/checkstyle/journey_checks.xml`. **Do not suppress violations without
adding to `config/checkstyle/checkstyle-suppressions.xml`.**

Tests use **Testcontainers** for MongoDB — no manual DB setup needed for unit/integration tests.

---

## Architecture


### Package structure (enforced by ArchUnit — see `ApplicationArchitectureTest`)

```
com.github.nramc.dev.journey.api
├── infrastructure/  # Cross-cutting config, event republisher, OpenAPI config, security, MongoDB, timezone, etc.
│   ├── event/       # EventRepublisher, EventRepublisherProperties
│   ├── openapi/     # OpenApiDocumentationConfig
│   ├── security/    # WebSecurityConfig, WebAuthnConfig, CorsProperties
│   ├── mongodb/     # MongoConfig, JacksonBased*Converter
│   ├── actuator/    # ApplicationProperties
│   ├── config/      # InfrastructureConfig
│   ├── web/         # GlobalRestExceptionHandler, MVC controllers
│   └── timezone/    # TimezoneInitialization
├── config/          # @Configuration + @Bean declarations ONLY (all beans wired here)
│   ├── security/    # WebSecurityConfig, WebAuthnConfig, CorsProperties
│   └── ...          # CloudinaryConfig, TelegramConfig, TotpConfig, MailConfig, etc.
├── core/
│   ├── app/health/  # Custom HealthIndicator impls (e.g. CloudinaryHealthIndicator)
│   ├── domain/      # Pure domain records/enums — no framework deps, no outward deps
│   ├── journey/     # Journey aggregate (Journey record, Visibility, JourneyAuthorizationManager)
│   ├── jwt/         # JwtGenerator + JwtProperties (token creation)
│   ├── security/
│   │   └── webauthn/ # WebAuthnService (passkey/FIDO2 registration & authentication)
│   ├── usecase/     # Business logic — accessed only by web resources and config
│   │   └── notification/ # NotificationService interface; injected as List<NotificationService>; impls: EmailNotificationService, TelegramNotificationService
│   ├── services/    # MailService (infrastructure service)
│   ├── exceptions/  # BusinessException, TechnicalException, NonTechnicalException
│   ├── validation/  # Custom constraint annotations (e.g. @ValidateVisibilities)
│   └── utils/       # Stateless utilities (no deps on web/gateway/repository/usecase)
├── gateway/         # External integrations: Cloudinary, Telegram
├── repository/      # MongoDB entities + Spring Data repos + converters
│   ├── journey/     # JourneyEntity, JourneyRepository, converters
│   └── user/        # AuthUser, UserRepository, credential/code/attributes sub-packages
├── web/
│   ├── exceptions/  # GlobalRestExceptionHandler (@RestControllerAdvice → ProblemDetail)
│   └── resources/
│       ├── mvc/     # Thymeleaf MVC controllers (home page)
│       └── rest/    # @RestController classes (must end with "Resource")
│           └── ai/  # AI resources (ChatClient-based); paths are hardcoded strings, not Resources.java constants
└── migration/       # Data migration rules (excluded from coverage)
```


**Key enforced rules:**
### Event Republisher

- Incomplete events are automatically retried by a scheduled republisher (`infrastructure/event/EventRepublisher.java`).
- Retry logic and batch size are configured in `infrastructure/event/EventRepublisherProperties.java` and wired in `infrastructure/config/InfrastructureConfig.java`.

- No `@Service`, `@Component`, or `@Repository` stereotypes (except `UserDetailsManager` impl) — use `@Bean` in
  `config/`
- No field-level `@Autowired` — constructor injection only, wired via `config/` classes
- No cyclic dependencies within `core.*` packages
- All `@Bean` methods must live in `..config..` classes annotated with `@Configuration`
- `Repository` classes may be accessed directly from `resources.rest.users.find` (ArchUnit allows this exception)
- `Entity` classes may be accessed directly from `resources.rest.journeys`, `core.journey.security`, `usecase`,
  `repository`, and `migration` (ArchUnit `ruleLimitRepositoryEntityDependant`)

### Security model

- Stateless JWT (RSA key pair) + HTTP Basic; OAuth2 resource server
- Roles: `GUEST_USER`, `AUTHENTICATED_USER`, `MAINTAINER`, `ADMINISTRATOR`
- All route permissions defined in `WebSecurityConfig`; all endpoint paths as constants in `Resources.java`
- Journey visibility controlled by `JourneyAuthorizationManager` (per-resource authorization)
- **WebAuthn (passkey/FIDO2):** enabled via `WebAuthnConfig` + `WebAuthnService`; endpoints at `/webauthn/register`,
  `/webauthn/authenticate`, `/webauthn/manage`
- **TOTP MFA:** configured in `TotpConfig`; login flow — `POST /rest/login` returns an MFA challenge when MFA is
  enabled, completed with `POST /rest/mfa`

### Journey update API uses custom media types (content-negotiation dispatch)

```
PUT /rest/journey/{id}
  application/vnd.journey.api.basic.v1+json    → UpdateJourneyBasicDetailsResource
  application/vnd.journey.api.geo.v1+json      → UpdateJourneyGeoDetailsResource
  application/vnd.journey.api.images.v1+json   → UpdateJourneyImagesDetailsResource
  application/vnd.journey.api.videos.v1+json   → UpdateJourneyVideosDetailsResource
  application/vnd.journey.api.publish.v1+json  → PublishJourneyResource
```

### AI endpoints

AI resources live in `web/resources/rest/ai/` and are the **only** `@RestController` classes that hardcode their paths
instead of referencing `Resources.java` constants. `WebSecurityConfig` guards them with a wildcard:

```java
.requestMatchers(GET,  "/rest/ai/**").

access(authenticatedUserAuthorizationManager)
.

requestMatchers(POST, "/rest/ai/**").

access(authenticatedUserAuthorizationManager)
```

Current AI resources:

| Path                              | Class                       | Purpose                         |
|-----------------------------------|-----------------------------|---------------------------------|
| `GET /rest/ai/hello`              | `HelloWorldChatResource`    | Simple chat prompt pass-through |
| `POST /rest/ai/enhance-narration` | `NarrationEnhancerResource` | Enhance journey narration text  |

New AI resources should use hardcoded paths and are covered by the existing wildcard security rule.

### External integrations

| Service     | Gateway class           | Config properties                                                               |
|-------------|-------------------------|---------------------------------------------------------------------------------|
| Cloudinary  | `CloudinaryGateway`     | `service.cloudinary.*` / env vars                                               |
| Telegram    | `TelegramGateway`       | `service.telegram.*` / `TELEGRAM_*`                                             |
| AI (Gemini) | Spring AI OpenAI compat | `GEMINI_API_KEY`; model `gemini-2.5-flash`; dev uses local Ollama (`qwen2.5vl`) |
| Email       | `MailService`           | `spring.mail.*` / env vars                                                      |
| WebAuthn    | `WebAuthnService`       | `app.security.webauthn.*` (rp-id, origin)                                       |


### GeoJSON handling

MongoDB stores GeoJSON via custom Jackson converters in `repository/converters/`. The `geojson4j` library (
`io.github.nramc:geojson4j`) is used for all GeoJSON domain objects — do not use plain Maps or Strings for geometry.

---


## Key Files


- `web/resources/Resources.java` — canonical list of all API paths and custom media types
- `config/security/WebSecurityConfig.java` — all security rules in one place
- `ApplicationArchitectureTest.java` — ArchUnit rules; violations = build failure
- `application-dev.yml` — dev overrides (SSL, local JWT keys, Mailpit, Ollama AI)
- `application-workspace.yml` — workspace/CI profile (disables Docker Compose auto-start)
- `docker-compose.yml` — infrastructure for local dev (MongoDB, Mongo Express, Mailpit)
- `web/exceptions/GlobalRestExceptionHandler.java` — `@RestControllerAdvice` mapping
  `BusinessException` → 400/422, `TechnicalException` → 500, `NonTechnicalException` → 422
- `web/resources/rest/doc/RestDocCommonResponse.java` — composite annotation to attach standard
  OpenAPI error responses (401, 403, 400, 422, 500) to controller methods
- `src/test/resources/http-scripts/` — IntelliJ HTTP Client scripts for manual REST testing (journey CRUD, auth,
  my-account, users)
- `config/timezone/TimezoneInitialization.java` — forces JVM to UTC at startup; all date/time values must be
  UTC-compatible
- `infrastructure/openapi/OpenApiDocumentationConfig.java` — OpenAPI/Swagger documentation configuration
- `infrastructure/event/EventRepublisher.java` — scheduled republisher for incomplete events
- `infrastructure/config/InfrastructureConfig.java` — root infrastructure configuration and event republisher wiring

