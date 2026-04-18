# AGENTS.md — Journey API

## Project Overview

Java 21 + Spring Boot 4 **BFF (Backend For Frontend)** REST API for the [Journeys SPA](https://journey.codewithram.dev).
Persists geospatial journey data in MongoDB Atlas using GeoJSON (`geojson4j` library). Deployed via Docker to Render.

**Multi-module Maven project:**

- `journey-api-web` — the runnable Spring Boot application (all source code lives here)
- `journey-api-tests` — separate integration/contract test module with Allure results

---

## Developer Workflows

### Local development

```bash
# Start infrastructure (MongoDB + Mongo Express + Mailpit)
docker compose up -d

# Run app with dev profile (uses classpath JWT keys, local Mailpit, local Ollama AI)
./mvnw spring-boot:run -pl journey-api-web -Dspring-boot.run.profiles=dev
```

- Dev profile enables HTTPS (`keystore/localhost.p12`), JWT keys from classpath (`app.key`/`app.pub`), and Mailpit at
  `localhost:1025`
- Mongo Express UI: http://localhost:9090
- Mailpit UI: http://localhost:8025

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
├── config/          # @Configuration + @Bean declarations ONLY (all beans wired here)
├── core/
│   ├── domain/      # Pure domain records/enums — no framework deps, no outward deps
│   ├── journey/     # Journey aggregate (Journey record, Visibility, JourneyAuthorizationManager)
│   ├── usecase/     # Business logic — accessed only by web resources and config
│   ├── services/    # MailService (infrastructure service)
│   ├── exceptions/  # BusinessException, TechnicalException, NonTechnicalException
│   └── utils/       # Stateless utilities (no deps on web/gateway/repository/usecase)
├── gateway/         # External integrations: Cloudinary, Telegram
├── repository/      # MongoDB entities + Spring Data repos + converters
├── web/resources/   # @RestController classes (must end with "Resource")
└── migration/       # Data migration rules (excluded from coverage)
```

**Key enforced rules:**

- No `@Service`, `@Component`, or `@Repository` stereotypes (except `UserDetailsManager` impl) — use `@Bean` in
  `config/`
- No field-level `@Autowired` — constructor injection only, wired via `config/` classes
- No cyclic dependencies within `core.*` packages
- All `@Bean` methods must live in `..config..` classes annotated with `@Configuration`

### Security model

- Stateless JWT (RSA key pair) + HTTP Basic; OAuth2 resource server
- Roles: `GUEST_USER`, `AUTHENTICATED_USER`, `MAINTAINER`, `ADMINISTRATOR`
- All route permissions defined in `WebSecurityConfig`; all endpoint paths as constants in `Resources.java`
- Journey visibility controlled by `JourneyAuthorizationManager` (per-resource authorization)

### Journey update API uses custom media types (content-negotiation dispatch)

```
PUT /rest/journey/{id}
  application/vnd.journey.api.basic.v1+json    → UpdateJourneyBasicDetailsResource
  application/vnd.journey.api.geo.v1+json      → UpdateJourneyGeoDetailsResource
  application/vnd.journey.api.images.v1+json   → UpdateJourneyImagesDetailsResource
  application/vnd.journey.api.videos.v1+json   → UpdateJourneyVideosDetailsResource
  application/vnd.journey.api.publish.v1+json  → PublishJourneyResource
```

### External integrations

| Service     | Gateway class           | Config properties                       |
|-------------|-------------------------|-----------------------------------------|
| Cloudinary  | `CloudinaryGateway`     | `service.cloudinary.*` / env vars       |
| Telegram    | `TelegramGateway`       | `service.telegram.*` / `TELEGRAM_*`     |
| AI (Gemini) | Spring AI OpenAI compat | `GEMINI_API_KEY`; dev uses local Ollama |
| Email       | `MailService`           | `spring.mail.*` / env vars              |

### GeoJSON handling

MongoDB stores GeoJSON via custom Jackson converters in `repository/converters/`. The `geojson4j` library (
`io.github.nramc:geojson4j`) is used for all GeoJSON domain objects — do not use plain Maps or Strings for geometry.

---

## Key Files

- `web/resources/Resources.java` — canonical list of all API paths and custom media types
- `config/security/WebSecurityConfig.java` — all security rules in one place
- `ApplicationArchitectureTest.java` — ArchUnit rules; violations = build failure
- `application-dev.yml` — dev overrides (SSL, local JWT keys, Mailpit, Ollama AI)
- `docker-compose.yml` — infrastructure for local dev (MongoDB, Mongo Express, Mailpit)

