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

### Naming & Placement

- REST controllers must be annotated with `@RestController`, reside in `web/resources/`, and have class names ending
  with `Resource`
- All API path constants go in `web/resources/Resources.java`; custom media type constants go in `Resources.MediaType`
- New security rules (route permissions) go in `config/security/WebSecurityConfig.java` only

### Package boundaries

```
domain/    → no outward deps (no usecase/config/service/gateway/repository/web)
usecase/   → only accessed by web/resources/ and config/
gateway/   → only accessed by config/, usecase/, services/, resources/, app.health/
repository/Entity → only accessed by repository/, migration/, usecase/, resources.rest.journeys/, core.journey.security/
utils/     → no deps on web/gateway/service/repository/usecase
```

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

### Security / Roles

- Four roles: `GUEST_USER`, `AUTHENTICATED_USER`, `MAINTAINER`, `ADMINISTRATOR`
- Stateless JWT (RSA key pair) + HTTP Basic auth; OAuth2 resource server
- When adding a new endpoint, register its path in `Resources.java` and add an authorization rule in
  `WebSecurityConfig.securityFilterChain()`; `anyRequest().denyAll()` is the default fallback

---

## Testing

- Tests use **Testcontainers** for MongoDB — no local DB needed
- Spring Cloud Contract stubs live in `src/test/resources/contracts/`; base class is `JourneyApiContractBase`
- Architecture rules are in `ApplicationArchitectureTest` — run `./mvnw verify` to check; violations fail the build
- Checkstyle config: `config/checkstyle/journey_checks.xml`; suppressions:
  `config/checkstyle/checkstyle-suppressions.xml`

---

## Environment / Config

| Profile | JWT keys                          | Mail                     | AI model                    |
|---------|-----------------------------------|--------------------------|-----------------------------|
| `dev`   | classpath (`app.key` / `app.pub`) | Mailpit `localhost:1025` | Local Ollama (`qwen2.5vl`)  |
| `prod`  | `/etc/secrets/jwt-private.key`    | SMTP via env vars        | Gemini via `GEMINI_API_KEY` |

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

