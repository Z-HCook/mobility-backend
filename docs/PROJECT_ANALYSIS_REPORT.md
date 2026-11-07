# Project Analysis Report — Wasel Palestine Mobility Backend

Review date: November 7, 2026. Baseline: local `master`, commit `468a957` (short hash). The working tree was clean before this work. Scope: every tracked application source file, Maven configuration/wrappers, runtime properties, Git metadata relevant to publication, workspace metadata, and the previous README.

## Evidence and verification boundaries

**Implemented** below means an executable code path is present and inspected. It does not imply successful runtime verification. **Partial** means code exists but is incomplete, disconnected, or has a material correctness limitation. **Unverified** means repository evidence cannot substantiate the claim.

The Maven test lifecycle compiled all 72 main Java files successfully, but there are no test sources and Surefire reported “No tests to run.” The application was not started and no database or routing/weather requests were sent. Existing credentials were not used or reproduced. Live schema, production deployment, external documentation, credentials' validity, performance, and remote branch state were not verified.

Source paths in this report are relative links. Package references point to inspected directories; findings cite the relevant files or methods.

## 1. Project overview and problem

Wasel Palestine is a single Java/Spring Boot backend intended to structure community mobility information in Palestine. Its main workflow is report submission, duplicate detection and voting, followed by moderator verification into incidents and optional checkpoint history. Clients can retrieve incidents, maintain location/type subscriptions, and request travel estimates.

This addresses fragmented information about disruptions and checkpoints by storing reports and verified events in a common data model. It provides API infrastructure rather than a complete navigation product: no frontend, notification delivery, independently validated travel model, or deployment system is included.

Evidence: [POM description](../pom.xml), [ReportService](../src/main/java/com/wasel/backend/service/ReportService.java), [IncidentService](../src/main/java/com/wasel/backend/service/IncidentService.java), [RouteEstimationService](../src/main/java/com/wasel/backend/service/RouteEstimationService.java).

## 2. Functionality and implementation status

| Capability | Status and observed behavior | Evidence |
| --- | --- | --- |
| Login | Implemented; email lookup and direct password equality produce one JWT | [AuthService](../src/main/java/com/wasel/backend/auth/AuthService.java) |
| User management | Implemented authenticated create/list/get/delete; accepts/returns entities | [UserController](../src/main/java/com/wasel/backend/controller/UserController.java) |
| Reports | Implemented transactional submission, user lookup, duplicate processing, and activity write | [ReportService](../src/main/java/com/wasel/backend/service/ReportService.java) |
| Credibility | Partial; duplicate insertion and voting use different scoring algorithms | [ReportService](../src/main/java/com/wasel/backend/service/ReportService.java), [VoteService](../src/main/java/com/wasel/backend/service/VoteService.java) |
| Voting | Implemented +1/-1 validation, repeat-vote lookup, score update, moderation/activity writes; concurrency/atomicity gaps remain | [VoteService](../src/main/java/com/wasel/backend/service/VoteService.java) |
| Verification | Implemented pending-report verification, incident creation, report link update, and checkpoint history; trusts submitted moderator ID | [IncidentService](../src/main/java/com/wasel/backend/service/IncidentService.java) |
| Incident management | Implemented paged retrieval/filtering, status update, close, delete | [IncidentController](../src/main/java/com/wasel/backend/controller/IncidentController.java) |
| Checkpoints | Implemented admin creation and history queries; verification can insert duplicate history rows | [CheckpointService](../src/main/java/com/wasel/backend/service/CheckpointService.java) |
| Subscriptions | Implemented creation, lookup by user ID, deletion; default radius 5 km | [SubscriptionService](../src/main/java/com/wasel/backend/service/SubscriptionService.java) |
| Alerts | Partial; radius/type matching and persistence exist, but no workflow caller, alert controller, or delivery transport | [AlertService](../src/main/java/com/wasel/backend/service/AlertService.java), [Alerts](../src/main/java/com/wasel/backend/service/Alerts.java) |
| Route estimates | Implemented OSRM request plus incident/weather heuristics; no live verification | [RouteEstimationService](../src/main/java/com/wasel/backend/service/RouteEstimationService.java) |
| Avoid areas | Partial; eight circular boundary points per area are mandatory waypoints, not exclusion geometry | [RoutingService](../src/main/java/com/wasel/backend/service/RoutingService.java) |
| Caching / rate limits | Implemented selectively and locally, with correctness/coverage limitations | [services](../src/main/java/com/wasel/backend/service) |
| Refresh token / public registration | Not implemented; RegisterRequest exists without a registration controller | [auth](../src/main/java/com/wasel/backend/auth), [RegisterRequest](../src/main/java/com/wasel/backend/dto/RegisterRequest.java) |
| Docker / k6 / deployment workflow | Previous README claims unsupported by committed artifacts | Repository inventory |

### Report and verification rules

Report matching uses category and a time window from 30 minutes before to 30 minutes after the server's current time. Native queries return at most 50 cross-user candidates or 10 same-user candidates, without ordering. Haversine distance below 0.2 km counts as nearby. A same-user match raises a business-rule exception.

Cross-user duplicates point to a root report. Insertion increases that root's duplicate count and sets its score to `duplicates + voteCount * 0.7`. At 15 or above, the root is marked promoted. This counts vote rows, including negative votes. Separately, voting overwrites credibility with the signed sum of all votes and does not apply the same promotion calculation.

Verification checks the report is pending and the supplied moderator record has role admin/moderator. It creates a MEDIUM-severity VERIFIED incident. Category mapping is traffic → DELAY, safety → ACCIDENT, weather → WEATHER, otherwise CLOSURE. It marks the report verified and links its incident. Both `logVerification` and `handleCheckpoint` write history for a linked checkpoint, causing two writes in the same verification flow.

## 3. Technology stack and versions

| Component | Verified version or configuration source |
| --- | --- |
| Java target | 21 in [pom.xml](../pom.xml) |
| Spring Boot parent | 3.5.11 in POM |
| Application artifact | `com.wasel:mobility-backend:0.0.1-SNAPSHOT` |
| Spring Web, Security, Data JPA, Cache | Direct Boot starters; individual transitive versions managed by parent, not independently enumerated |
| Hibernate / PostgreSQL JDBC driver | ORM and runtime driver managed by parent; database server version unspecified |
| JJWT API / implementation / Jackson | 0.11.5 explicitly pinned |
| Bucket4j core | 7.6.0 explicitly pinned |
| Caffeine | 3.2.3 resolved in actual Maven validation |
| Lombok | Optional dependency and compiler annotation processor; managed version |
| Boot configuration processor | Optional dependency; no custom property-binding class found |
| Test libraries | Boot starter test and Spring Security test, test scope |
| Maven Wrapper | Wrapper 3.3.4; distribution Maven 3.9.12 in [wrapper properties](../.mvn/wrapper/maven-wrapper.properties) |
| Validation environment | Oracle JDK 21.0.5, installed Maven 3.9.9, Windows 11 |
| External services | OSRM public routing endpoint; OpenWeatherMap current weather API; Supabase-hosted PostgreSQL configured |

No Redis, messaging broker, PostGIS dependency, Flyway/Liquibase, Actuator, OpenAPI generator, or container tooling is configured. No versions are asserted for remote services.

## 4. Architecture and package structure

The application is a layered monolith with constructor injection. [MobilityBackendApplication](../src/main/java/com/wasel/backend/MobilityBackendApplication.java) enables component scanning and caching.

| Package | Responsibility and observations |
| --- | --- |
| [auth](../src/main/java/com/wasel/backend/auth) | Login DTOs, controller/service, JWT helper and request filter |
| [config](../src/main/java/com/wasel/backend/config) | Security filter chain, BCrypt bean, shared RestTemplate |
| [controller](../src/main/java/com/wasel/backend/controller) | Seven resource controllers; auth has its own controller |
| [dto](../src/main/java/com/wasel/backend/dto) | Inputs and selected outputs; mix of public fields and accessors |
| [usecase](../src/main/java/com/wasel/backend/usecase) | Seven thin wrappers delegating to services |
| [service](../src/main/java/com/wasel/backend/service) | Domain workflows, persistence coordination, caches, throttling, external HTTP |
| [repository](../src/main/java/com/wasel/backend/repository) | Ten JpaRepository interfaces, derived queries, JPQL, native report queries |
| [model](../src/main/java/com/wasel/backend/model) | Ten JPA entities, distance/type helpers |
| [exception](../src/main/java/com/wasel/backend/exception) | Custom runtime exceptions and controller advice |

Requests pass through Spring Security before controllers. Some controllers use use-case wrappers, others call services, and some mix both. This is not a strict ports-and-adapters architecture: domain logic depends directly on Spring/JPA and concrete services. `InsertReportUseCase` is not used by ReportController. No utility or dedicated test package is present.

## 5. Main components and responsibilities

- **ReportService:** report construction, same-user abuse checks, cross-user duplicate association, promotion scoring, activity logging, pending-state validation, verification links.
- **VoteService:** duplicate-vote detection, vote persistence, signed credibility sum, moderation and activity records.
- **IncidentService:** verification orchestration, incident lifecycle, paged queries, and counting recent nearby incidents.
- **CheckpointService:** principal-based admin check, creation, history/date queries, and verification history writes.
- **SubscriptionService:** subscription persistence with user existence checks and default radius.
- **AlertService:** matching subscriptions by incident type and radius and writing unread alerts; disconnected from incident service.
- **UserService:** user CRUD/caches and moderator-record validation.
- **RoutingService:** OSRM URL construction, circular waypoint generation, response extraction into kilometers/minutes.
- **WeatherService:** current conditions at each endpoint, fallback text on failures, and additive travel-time adjustments.
- **RouteEstimationService:** route orchestration; 5% incident delay per nearby incident, capped at 30%, then weather adjustment.
- **RateLimitingService:** process-local ConcurrentHashMap of Bucket4j buckets.
- **AppConfig:** shared synchronous HTTP client with 5-second connection and read timeouts.
- **SecurityBeans:** BCrypt encoder declaration; not used by current account creation/login.

Evidence: [services](../src/main/java/com/wasel/backend/service), [configuration](../src/main/java/com/wasel/backend/config).

## 6. Database design

Entity mappings establish ten tables, all with generated identity primary keys:

| Table / model | Important data and references |
| --- | --- |
| users / User | Name, unique non-null email, non-null role/password, creation timestamp |
| reports / Report | Author ID, category/type, coordinates, description/title, status, credibility, duplicate root/count, linked incident/checkpoint IDs, promotion flag, timestamps |
| incidents / Incident | Title/type/severity/status, scalar reporter/verifier/checkpoint IDs, coordinates, creation/update/close timestamps |
| checkpoints / Checkpoint | Name/location/description, mapped creator User, creation timestamp |
| checkpoint_history / CheckpointHistory | Mapped Checkpoint and Incident, insertion timestamp |
| report_votes / ReportVote | Scalar report/user IDs, signed vote type, timestamp |
| report_moderation_logs / ReportModerationLog | Scalar report/moderator IDs, action/note, timestamp |
| user_activity / UserActivity | Scalar user/report/vote IDs, action type, timestamp |
| subscriptions / Subscriptions | Scalar user ID, region, coordinates, radius, incident type, OffsetDateTime creation |
| alerts / Alert | Scalar user/incident IDs, message, read flag, timestamp |

The explicit JPA relationships are many checkpoints to one creator user and many history rows to one checkpoint/incident. All other ID fields describe logical relationships without JPA association declarations. Actual remote foreign keys, indexes, checks, and schema contents cannot be inferred from these scalar fields.

Most timestamps use LocalDateTime; subscriptions use OffsetDateTime. Incident lifecycle callbacks set creation/update times. No version columns, explicit voting composite uniqueness constraint, or geospatial indexes are declared. JPA uses automatic schema update rather than migrations.

[Models](../src/main/java/com/wasel/backend/model), [repositories](../src/main/java/com/wasel/backend/repository), and [properties](../src/main/resources/application.properties) are the evidence. Live database verification was deliberately not performed.

## 7. Authentication, authorization, and security

[SecurityConfig](../src/main/java/com/wasel/backend/config/SecurityConfig.java) disables CSRF and uses stateless sessions. Login is permitted publicly; all unmatched routes require authentication. [JwtAuthFilter](../src/main/java/com/wasel/backend/auth/JwtAuthFilter.java) parses Bearer tokens, sets the principal to the user ID string, and assigns the exact role claim as a granted authority. It does not recheck account existence or current role against the database. Invalid tokens return 401; user/role debug messages and stack traces are printed.

[JwtService](../src/main/java/com/wasel/backend/auth/JwtService.java) signs tokens with a hardcoded HMAC key, includes subject/role/issued-at, and sets 30-day expiry. No refresh, revocation, issuer/audience validation policy, or key rotation is implemented.

Material findings:

1. **Embedded sensitive values:** database credentials in application.properties, signing secret in JwtService, and API key in WeatherService. Values are not repeated in either document. Validity was not tested. Rotation/remediation must occur separately; this documentation task cannot change runtime files.
2. **Password handling:** AuthService compares plaintext-equivalent values with String.equals; UserService persists input directly. The BCrypt bean is unused.
3. **Password disclosure:** User has an ordinary public password getter without serialization suppression. User endpoints and nested checkpoint/history entities can serialize it.
4. **Role-rule mismatch:** `/api/incidents/**` is restricted to admin, but real incident routes start `/api/v1/incidents`; those fall through to authenticated access. `/insert/**` also has no matching controller. The checkpoint controller rule is commented out, although service creation correctly checks the principal's database role.
5. **Identity/ownership gaps:** reports, votes, subscriptions, and verification accept IDs from caller input. Moderator lookup validates the submitted record's role, not its equality to the token subject. User CRUD and subscription lookup/deletion lack ownership/admin enforcement.
6. **User mass assignment:** creation accepts User including role/password/ID fields, and is available to any authenticated caller. There is no public registration or safe initial administrator provisioning flow.
7. **Error leakage:** generic handler returns exception messages and prints stack traces; filter errors bypass controller advice.
8. **External HTTP:** OSRM URL uses HTTP. No custom CORS policy, login throttle, or distributed abuse controls are found.

Sources: [AuthService](../src/main/java/com/wasel/backend/auth/AuthService.java), [User](../src/main/java/com/wasel/backend/model/User.java), [UserService](../src/main/java/com/wasel/backend/service/UserService.java), [controllers](../src/main/java/com/wasel/backend/controller), [GlobalExceptionHandler](../src/main/java/com/wasel/backend/exception/GlobalExceptionHandler.java).

## 8. API structure and business contracts

The [README API table](../README.md#api-reference) lists every controller mapping. Eight controllers expose 23 method/path combinations. Prefixes are `/api/v1/auth`, `/api/v1/incidents`, `/api/v1/subscriptions`, `/api/v1/routes`, `/api/users`, `/api/votes`, `/report`, and `/checkpoints`.

Important request contracts:

| Operation | Input |
| --- | --- |
| Login | email, password |
| Report submission | userId, category, description, latitude, longitude, type, optional linkedcheckpoint (exact field spelling) |
| Verification | reportId, moderatorId |
| Vote | userId, reportId, voteType (+1/-1) |
| Checkpoint creation | name, latitude, longitude, description; createdById exists but service derives creator from principal |
| Subscription creation | userId, region, latitude, longitude, radiusKm, incidentType |
| Route estimation | startLat, startLng, endLat, endLng, optional avoidAreas with name/lat/lng/radius |

Incident lists default to page 0 and size 10, returning content lists without page metadata. Category filters map through the same category-to-type function as verification. Checkpoint date filtering requires both bounds to apply.

Route results report distance in km, duration in minutes, and factors. Incidents from the last 30 minutes within 3 km of either endpoint affect estimates, regardless of status; the full route corridor is not checked. Weather penalties are +5 minutes for rain, +7 for fog, +2 for clouds, in that precedence order. Unknown weather adds a factor without time penalty. Calls are synchronous and sequential.

The custom exception hierarchy maps validation to 400, missing resource to 404, unauthorized to 403, business rule to 422. Catch-all handling returns 500 as a message body. Verification wraps even specific exceptions into RuntimeException; subscription controllers also override some statuses locally. There is no uniform error contract or Jakarta Bean Validation usage.

[Controllers](../src/main/java/com/wasel/backend/controller), [DTOs](../src/main/java/com/wasel/backend/dto), [exception advice](../src/main/java/com/wasel/backend/exception/GlobalExceptionHandler.java).

## 9. Performance features and limitations

### Implemented mechanisms

- Caching enabled globally; Caffeine maximum 500 entries and five-minute expire-after-write per cache.
- Incident list cache keys include page/size; category caching uses only category.
- User list/individual caches with eviction on user mutations; date-range checkpoint caching.
- Bucket4j capacity/refill 1,000 tokens every minute. IP keys are shared across selected controller operations; report submission uses IP plus supplied user ID.
- Incident repository pagination and descending creation order; report matching limits candidate rows.
- Hikari maximum 25 connections/minimum idle 10; open-in-view disabled.
- HTTP connection/read timeout 5 seconds each; MVC async timeout configured to 5 seconds.
- Transactional report submission, verification, subscription mutations, and incident writes.

### Correctness and scale limits

Category cache keys omit page and size, allowing requests for different pages to receive the same cached content. Incident writes do not evict incident controller caches. History writes do not invalidate date-range history caches. ReportService's private `@Cacheable` method is called internally and does not pass through Spring's cache proxy. AlertService's subscriber lookup is annotated with eviction, not caching, and is not used in alert creation.

Rate-limit storage has no expiry/cleanup and is local to each process; it is not a global quota across replicas. It is absent on login, routes, incident reads/mutations, and the alternate verification endpoint. User-by-ID reads fetch before consuming a token. Changing a submitted report user ID changes its limiter key.

Candidate limits without ordering can miss genuine duplicates; root traversal can loop indefinitely if a reference is missing or cyclic. Concurrent score/vote/duplicate updates have no optimistic locking. VoteService lacks a transaction spanning its writes, and repeat-vote checks are not backed by a declared unique constraint.

Synchronous external HTTP calls can exceed the async MVC timeout in total; that setting does not impose an end-to-end deadline on synchronous controllers. SQL and cache DEBUG logging can add overhead and expose operational details.

No benchmark results are verified. The previous README's k6 scenarios, 200-VU spike, and sub-5 ms p95 statement lack scripts/results in this tree.

Evidence: [runtime properties](../src/main/resources/application.properties), [RateLimitingService](../src/main/java/com/wasel/backend/service/RateLimitingService.java), [IncidentController](../src/main/java/com/wasel/backend/controller/IncidentController.java), [ReportService](../src/main/java/com/wasel/backend/service/ReportService.java), [CheckpointService](../src/main/java/com/wasel/backend/service/CheckpointService.java).

## 10. Testing strategy and observed validation

Existing assets: test dependencies in the POM, a Postman workspace reference, and empty global values. No committed src/test tree, request collection, k6 script, CI workflow, coverage report, test database profile, or integration harness was found.

Executed on November 7, 2026:

| Check | Actual result |
| --- | --- |
| `java -version`, `mvn -version` | JDK 21.0.5 and Maven 3.9.9 available |
| `mvn -B -o test` | Failed before compilation because Caffeine 3.2.3 was not cached |
| `mvn -B test` | Downloaded missing dependencies, compiled 72 source files, BUILD SUCCESS, “No tests to run” |
| Compiler diagnostics | Unchecked/unsafe operations warning referring to WeatherService |
| Startup, live APIs, PostgreSQL, OSRM/weather | Not exercised |
| Wrapper distribution execution / JAR packaging | Commands checked against wrapper/POM; not executed |

Recommended future tests: ownership/role matrix and password serialization; duplicate time/distance boundaries and cycles; concurrent duplicate votes; verification rollback and single history insertion; cache pagination/invalidation; subscription radius/type matching; route unit conversions, external failures and avoidance semantics. Use isolated PostgreSQL integration tests and mocked external services before any load test.

This is a proposed strategy, not implemented test coverage.

Documentation validation checked 67 relative links and heading anchors, balanced code fences, and omission of the four embedded database username/password, signing-secret, and weather-key values. Final whitespace and diff review covered both files; no runtime source changes were made.

## 11. Code quality, strengths, and architectural decisions

Strengths supported by source include constructor injection, separation into packages, Spring Data persistence abstractions, DTO inputs for several workflows, transactions around major multi-write flows, explicit application exceptions, query pagination, bounded local caches, and HTTP timeout configuration. The report verification workflow shows orchestration across reports, users, incidents, and checkpoints.

Tradeoffs and debt include mostly pass-through use cases, inconsistent use of controllers/services, entity responses, public-field/accessor DTO inconsistency, string statuses/roles instead of constrained types, raw Map/List external responses, duplicated category/distance logic, and debug printing. User has unused methods that return null or dereference a null repository; CheckpointHistoryResponse duplicates entity mapping annotations rather than serving as a clean output contract. RegisterRequest and some imports/methods are unused.

AlertService calculates a normalized type but queries with the original type; region is stored but not consulted, and null subscription coordinates can fail. No event or service call connects alert generation to verification.

Evidence: [User](../src/main/java/com/wasel/backend/model/User.java), [CheckpointHistoryResponse](../src/main/java/com/wasel/backend/dto/CheckpointHistoryResponse.java), [RoutingService](../src/main/java/com/wasel/backend/service/RoutingService.java), [AlertService](../src/main/java/com/wasel/backend/service/AlertService.java), [use cases](../src/main/java/com/wasel/backend/usecase).

## 12. Missing features and prioritized improvements

These are recommendations, not implemented behavior:

1. Rotate embedded secrets, externalize configuration, hash/match passwords with the configured encoder, and exclude credentials from all response DTOs.
2. Correct security matchers, bind acting IDs to the authenticated principal, restrict role assignment and user administration, and enforce resource ownership.
3. Add security/integration tests and database migrations with appropriate uniqueness, foreign-key, and concurrency controls.
4. Make voting atomic, unify credibility/promotion rules, guard duplicate-root traversal, order candidate queries, and remove duplicate history insertion.
5. Correct cache keys/invalidation and apply consistent, bounded, principal-aware rate limits.
6. Add input constraints, consistent error bodies/statuses, typed external responses, and structured logging.
7. Connect alert generation to a defined incident event/transaction flow and implement retrieval/delivery only if required.
8. Replace avoidance waypoints with routing support for exclusion geometry, validate coordinate ranges, and document heuristic uncertainty.
9. Add health/metrics, deployment artifacts, isolated configuration profiles, and reproducible performance tests before operational claims.

## 13. Exact setup and execution requirements

The [README setup](../README.md#installation-and-configuration) contains executable shell examples and variable names without real credentials.

Required: JDK 21; wrapper or installed Maven; dependency network access; an isolated PostgreSQL database with table create/update permissions; and network access to routing/weather services for route requests. Docker is not required by any committed execution path.

Standard Spring environment overrides: SPRING_DATASOURCE_URL, SPRING_DATASOURCE_USERNAME, SPRING_DATASOURCE_PASSWORD, optional SERVER_PORT and SPRING_JPA_HIBERNATE_DDL_AUTO. They override existing property names. No custom environment binding exists for the hardcoded JWT/weather key.

Defaults include 0.0.0.0:8081, ddl-auto=update, SQL logging, Caffeine, disabled response compression, and Hikari settings noted above. The configured remote JDBC URL includes prepareThreshold=0. Do not reuse embedded remote credentials.

Run `./mvnw spring-boot:run` on POSIX or `.\mvnw.cmd spring-boot:run` in PowerShell. `sh mvnw` is an alternative where executable permission is absent. Build with `./mvnw package`; the Boot Maven plugin produces the executable artifact `target/mobility-backend-0.0.1-SNAPSHOT.jar`. These wrapper/packaging commands were inspected, not executed. Installed Maven's test lifecycle was executed successfully.

There are no seed scripts or bootstrap users. Public registration is absent, and authenticated user creation cannot initialize an empty database by itself. A developer must separately provision an isolated test account consistent with current direct-password comparison. There is no claim that untouched configuration constitutes a secure deployment.

## 14. Engineering skills demonstrated

The implementation demonstrates Java 21/Maven project configuration, Spring MVC REST controllers, dependency injection, Spring Data JPA modeling and queries, transactional workflow coordination, JWT filter integration, authority rules, Caffeine annotations, Bucket4j token buckets, pagination, exception advice, external HTTP integration, and Haversine geometry.

The demonstrated security/cache/route mechanisms have the limitations documented above. The repository does not substantiate claims of production readiness, distributed architecture, comprehensive testing, measured scalability, or container deployment expertise.

## Documentation changes and publication status

The rewritten README replaces unsupported refresh-token, universal-versioning, Docker, real-time notification, and benchmark claims with code-grounded behavior. It adds setup overrides, endpoint contracts, source links, architecture/database summaries, verification limits, and known security issues. Existing university, supervisor, and four contributor credits are preserved.

Only README.md and this report are included in the documentation commit. No application or runtime configuration changes were made by this documentation task. A subsequent unrelated local pom.xml edit changes the Java target from 21 to 25; it is preserved outside the documentation commit. The analysis and compilation result describe the committed Java 21 baseline reviewed above.

At review start, the local branch was master tracking origin/master, with origin/HEAD pointing to origin/master. No local or cached origin/main reference was present. A subsequent read-only query with git ls-remote confirmed that origin/HEAD points to master, origin/master remains at 468a9571f9a3e96fbd5d6a70b7634483d0ec48b9, and origin/main does not exist. The user subsequently explicitly authorized publication to main, including proceeding with the existing source credentials unchanged.

Publication was initially paused under the original sensitive-data stop condition. Following explicit user authorization, the documentation is prepared for a new main branch from the reviewed master baseline. Because origin/main does not exist, publication can create that branch without overwriting remote history. Embedded credentials remain an unresolved application issue outside this documentation-only change. The resulting commit hash and push outcome are recorded in Git and the accompanying completion message.
