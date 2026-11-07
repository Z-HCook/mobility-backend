# Wasel Palestine — Mobility Backend

A Java 25 and Spring Boot REST backend for community mobility reports, checkpoint history, incident verification, subscriptions, and route estimates in Palestine.

The project structures information about travel disruptions so clients can retrieve incidents and moderators can verify reports. It is a development project with security and correctness limitations documented below.

## Features

- Submit location-based reports, detect nearby duplicates, and reject recent repeat reports by the same user.
- Vote on reports, update credibility scores, and record voting/activity logs.
- Verify pending reports into incidents; list, filter, update, close, and delete incidents.
- Create checkpoints as an administrator and retrieve checkpoint history with optional time bounds.
- Create, list, and delete location/type subscriptions.
- Estimate driving distance and duration using OSRM, recent incidents near route endpoints, and OpenWeatherMap conditions.
- Cache selected reads with Caffeine and throttle selected operations with Bucket4j.

Alert persistence logic exists but is not connected to the incident workflow and has no controller or delivery channel. Route `avoidAreas` creates circular waypoints; it does **not** guarantee exclusion of those areas.

## Technology stack

| Technology | Version / purpose |
| --- | --- |
| Java | 21 |
| Spring Boot | 3.5.11 |
| Spring Web, Data JPA, Security, Cache | Versions managed by the Boot parent |
| PostgreSQL / Hibernate | JDBC persistence and JPA mappings; Supabase-hosted PostgreSQL configured |
| JJWT | 0.11.5; signed bearer tokens |
| Caffeine | 3.2.3 resolved during validation; local caching |
| Bucket4j | 7.6.0; token-bucket rate limits |
| Lombok | Annotation processor; managed version |
| Spring Boot Test / Security Test | Declared dependencies; no committed test sources |
| Maven Wrapper | Maven 3.9.12, wrapper 3.3.4 |

Database server version is unspecified. Docker files, k6 scripts, and OpenAPI/Swagger definitions are not included.

## Architecture

A single Spring Boot application uses controllers, optional use-case wrappers, services, and Spring Data repositories. Some controllers call services directly.

```text
HTTP client
  -> Spring Security / JWT filter
  -> Controller
  -> Use case (where used) -> Service
  -> JPA repository -> PostgreSQL

Route estimation -> OSRM + OpenWeatherMap
```

```text
src/main/java/com/wasel/backend/
  auth/          Login, JWT generation, bearer-token filter
  config/        Security rules, password encoder, HTTP client
  controller/    REST endpoints
  dto/           Requests and selected responses
  exception/     Custom exceptions and controller advice
  model/         Ten JPA entities
  repository/    Spring Data JPA queries
  service/       Workflows, caches, throttling, integrations
  usecase/       Selected service wrappers
src/main/resources/application.properties
pom.xml
.mvn/wrapper/maven-wrapper.properties
postman/         Workspace globals; no API request collection
docs/PROJECT_ANALYSIS_REPORT.md
```

## Database design

The mapped tables are `users`, `reports`, `incidents`, `checkpoints`, `checkpoint_history`, `report_votes`, `report_moderation_logs`, `user_activity`, `subscriptions`, and `alerts`.

Checkpoint creators have a many-to-one association to users. History rows have many-to-one associations to checkpoints and incidents. Most other references are scalar IDs rather than mapped associations; the Java model does not establish every intended foreign key.

Hibernate uses `ddl-auto=update`. No migrations or seed data are committed. See the [entity models](src/main/java/com/wasel/backend/model) and [database analysis](docs/PROJECT_ANALYSIS_REPORT.md#6-database-design).

## Authentication and security

Login returns `{"token":"..."}`. Send it as `Authorization: Bearer <token>`. Tokens carry the user ID as subject and a role claim, with 30-day expiry. Refresh tokens, revocation, and public registration are not implemented.

Only `/api/v1/auth/login` is explicitly public. All other routes require authentication. Voting requires the exact authority `user`; checkpoint creation checks the authenticated database user's role for `admin`.

**Known limitations:** login compares passwords directly despite a BCrypt bean. User entities expose passwords through a getter and are returned by user endpoints and nested checkpoint responses. The incident admin matcher targets `/api/incidents/**`, while the controller uses `/api/v1/incidents`. Several workflows trust submitted user/moderator IDs without binding them to the authenticated principal.

Existing source contains database credentials, a JWT signing secret, and a weather API key. Values are intentionally omitted here. Rotation and source/configuration remediation are required before deployment. This documentation update does not change runtime code.

## API reference

Default base URL: `http://localhost:8081`. Prefixes are mixed; not all endpoints are versioned.

| Method | Path | Operation |
| --- | --- | --- |
| POST | `/api/v1/auth/login` | Email/password login |
| POST, GET | `/api/users` | Create/list users; authenticated |
| GET, DELETE | `/api/users/{id}` | Retrieve/delete a user |
| POST | `/report` | Submit a report |
| POST | `/api/votes` | Vote with userId, reportId, voteType (+1/-1) |
| POST | `/checkpoints` | Create a checkpoint as admin |
| GET | `/checkpoints/{id}/history` | Retrieve history |
| GET | `/checkpoints/{id}/timeperiod` | Filter with start/end timestamps |
| POST | `/api/v1/incidents`, `/api/v1/incidents/verify` | Verify using reportId and moderatorId |
| GET | `/api/v1/incidents` | List; defaults page=0, size=10 |
| GET, DELETE | `/api/v1/incidents/{id}` | Retrieve/delete an incident |
| GET | `/api/v1/incidents/category` | Filter by category, with page/size |
| GET | `/api/v1/incidents/status` | Filter by status, with page/size |
| PUT | `/api/v1/incidents/{id}/status` | Update via status query parameter |
| PUT | `/api/v1/incidents/{id}/close` | Close an incident |
| POST | `/api/v1/subscriptions/create` | Create a subscription |
| GET | `/api/v1/subscriptions/user/{userId}` | List subscriptions |
| DELETE | `/api/v1/subscriptions/{id}` | Delete a subscription |
| POST | `/api/v1/routes` | Estimate driving distance/duration |

History date filtering requires both bounds, for example `2026-11-01T00:00:00` and `2026-11-02T00:00:00`. Otherwise the controller retrieves unfiltered history.

Example requests for an isolated development instance (POSIX shell; TOKEN contains a locally obtained token):

```bash
curl -X POST http://localhost:8081/api/v1/auth/login \
  -H 'Content-Type: application/json' \
  -d '{"email":"developer@example.invalid","password":"<local-test-password>"}'

curl 'http://localhost:8081/api/v1/incidents?page=0&size=10' \
  -H "Authorization: Bearer $TOKEN"

curl -X POST http://localhost:8081/api/v1/routes \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"startLat":32.22,"startLng":35.26,"endLat":31.90,"endLng":35.20,"avoidAreas":[]}'
```

Route responses contain `distance` in kilometers, `duration` in minutes, and explanatory `factors`. These are source contracts, not live-tested examples.

Errors are inconsistent: custom validation/not-found/business-rule exceptions map to 400/404/422; custom unauthorized exceptions map to 403, invalid bearer tokens to 401, and generic exceptions to 500. Selected throttled endpoints return 429.

## Installation and configuration

Requirements: JDK 25; Maven Wrapper or installed Maven; dependency download access; an isolated PostgreSQL database with table create/update permissions; routing/weather network access for full route estimates.

```bash
git clone https://github.com/Z-HCook/mobility-backend.git
cd mobility-backend
```

Override the committed database settings before startup. Do not reuse embedded remote credentials.

| Environment variable | Purpose |
| --- | --- |
| SPRING_DATASOURCE_URL | PostgreSQL JDBC URL |
| SPRING_DATASOURCE_USERNAME | Development database user |
| SPRING_DATASOURCE_PASSWORD | Development database password |
| SPRING_JPA_HIBERNATE_DDL_AUTO | Schema behavior; default update |
| SERVER_PORT | HTTP port; default 8081 |

These standard Spring Boot overrides correspond to properties already present in the project.

Bash:

```bash
export SPRING_DATASOURCE_URL='jdbc:postgresql://localhost:5432/wasel_dev'
export SPRING_DATASOURCE_USERNAME='wasel_dev'
read -r -s -p 'Database password: ' SPRING_DATASOURCE_PASSWORD
export SPRING_DATASOURCE_PASSWORD
./mvnw spring-boot:run
```

Windows PowerShell:

```powershell
$env:SPRING_DATASOURCE_URL = 'jdbc:postgresql://localhost:5432/wasel_dev'
$env:SPRING_DATASOURCE_USERNAME = 'wasel_dev'
$env:SPRING_DATASOURCE_PASSWORD = [System.Net.NetworkCredential]::new('', (Read-Host 'Database password' -AsSecureString)).Password
.\mvnw.cmd spring-boot:run
```

Use `sh mvnw spring-boot:run` if the Unix script is not executable. Installed Maven also supports `mvn spring-boot:run`.

No JWT_SECRET or weather-key environment binding is implemented; those values are hardcoded in Java. No bootstrap administrator or public registration endpoint exists. Provision a separate test user in the isolated development database before login; current login expects direct password equality.

The application binds to `0.0.0.0:8081`. SQL logging and cache debug logging are enabled, open-in-view is disabled, and the Hikari pool allows 25 maximum connections with 10 minimum idle.

## Build, testing, and performance

```bash
./mvnw test
./mvnw package
java -jar target/mobility-backend-0.0.1-SNAPSHOT.jar
```

PowerShell equivalents are `.\mvnw.cmd test` and `.\mvnw.cmd package`. Keep database overrides set when running the JAR.

On November 7, 2026, `mvn -B test` successfully compiled 72 Java files and reported **No tests to run**. This verifies compilation, not automated coverage. Startup, live APIs, database behavior, external services, wrapper execution, and JAR packaging were not exercised.

Caffeine uses 500 entries per cache and five-minute expiry after write. Bucket4j uses 1,000 tokens refilled every minute, stored per instance. Enforcement is selective. Known cache issues include omitted pagination in category keys, missing incident mutation invalidation, and an ineffective private cached method.

The previous README's k6 workload, 200-VU, and sub-5 ms p95 claims have no committed scripts/results and are not presented as verified benchmarks.

## Additional documentation

- [Comprehensive project analysis](docs/PROJECT_ANALYSIS_REPORT.md)
- [Build configuration](pom.xml)
- [Runtime properties](src/main/resources/application.properties) — contains existing sensitive settings; do not reuse them.
- [Controller source](src/main/java/com/wasel/backend/controller)

## Contributors

Advanced Software Engineering — Spring 2026

An-Najah National University

Supervisor: Dr. Amjad AbuHassan

- Khadejah Al-Etyani
- Dana Ismail
- Zeina Hanani
- Masa Kanaze'
