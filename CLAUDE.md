# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project overview

Movie Feedback Collection System: a Spring Boot microservice backend + React frontend where admins manage a movie list and visitors leave feedback/ratings. Five independently deployable services, no shared database (each Spring Boot service owns its own in-memory H2 instance).

## Architecture

```
Frontend (React, :3000) → API Gateway (Spring Cloud Gateway, :8080) → Eureka (:8761) for discovery
                                    ├─→ lb://movie-service (:8081)
                                    └─→ lb://feedback-service (:8082)
```

- **eureka-server** (`backend/eureka-server`) — Netflix Eureka service registry. All other backend services register with it (`register-with-eureka: true`) and discover each other through it. Must be started first; the other services have `defaultZone: http://localhost:8761/eureka/` and will fail to route until it's up.
- **api-gateway** (`backend/api-gateway`) — Spring Cloud Gateway. Routes are defined explicitly in `application.yml` (`/api/movies/**` → `lb://movie-service`, `/api/feedback/**` → `lb://feedback-service`) plus a discovery locator for anything else. **This is the only layer that owns CORS** (`spring.cloud.gateway.globalcors` in its `application.yml`) — do not re-add `@CrossOrigin` to downstream controllers; Gateway intercepts preflight `OPTIONS` requests itself before proxying, and having CORS configured in two places produces a duplicate `Access-Control-Allow-Origin` header that browsers reject outright.
- **movie-service** (`backend/movie-service`) — CRUD + search/filter for movies (`MovieController` → `MovieService` → `MovieRepository`, JPA/H2). No security layer — it was previously wired with an incomplete `spring-boot-starter-security` setup that locked down every endpoint (including `/actuator/health`, breaking Docker/K8s health probes and all frontend calls) with no distinction between admin and visitor routes; this was removed rather than left half-configured. If admin-only write protection is reintroduced, it needs a real `SecurityFilterChain` plus a corresponding frontend auth flow (neither existed before).
- **feedback-service** (`backend/feedback-service`) — CRUD + queries for feedback (rating, by-movie, by-visitor, average rating, recent). Same layered structure as movie-service. `feedback.movieId` is not validated against movie-service (no cross-service existence check).
- **frontend** (`frontend/`) — React 18 + React Router + React Bootstrap. All API calls go through `frontend/src/services/api.js`, which hits the gateway at `REACT_APP_API_URL` (defaults to `http://localhost:8080`). Key views: `MovieList`, `MovieForm` (create/edit), `FeedbackForm`, `FeedbackList`, `AdminDashboard`. There is no login/auth UI anywhere in the frontend.

Each backend service is a standalone Maven project (own `pom.xml`, Spring Boot 3.2.0 / Java 17, Spring Cloud 2023.0.0) — there is no parent/multi-module `pom.xml` at the repo root, so Maven commands must be run from within each service directory.

## Common commands

Run from within the relevant `backend/<service>/` directory (no root-level Maven wrapper):

```bash
mvn test                       # run all tests for one service
mvn test -Dtest=ClassName      # run a single test class
mvn test -Dtest=ClassName#methodName   # run a single test method
mvn spring-boot:run            # run that service locally (uses in-memory H2, local Eureka)
mvn clean package -DskipTests  # build the jar without running tests (used by scripts/build.sh)
```

Frontend (`frontend/`):

```bash
npm install
npm start        # CRA dev server on :3000 (proxies to :8080 per package.json "proxy", but api.js uses an absolute base URL)
npm test          # Jest / React Testing Library
npm run build
```

### Running the full stack locally without Docker

Start in this order (each depends on the previous being reachable): `eureka-server` → `movie-service` + `feedback-service` (parallel) → `api-gateway` → `frontend`. `scripts/run-local.bat` automates this on Windows. With Docker available, `docker compose -f infrastructure/docker/docker-compose.yml up -d` does the same via `scripts/run-docker.bat` / `scripts/build.sh`; Compose health checks (`/actuator/health`) gate startup ordering via `depends_on: condition: service_healthy`, so a broken health endpoint on any one service blocks everything downstream of it.

### CI

`.github/workflows/ci-cd.yml` runs `mvn clean test` separately for each of the four backend services on every push/PR, then (on push only) builds and pushes Docker images, runs a Trivy scan, and deploys to Kubernetes (`develop` → staging, `main` → production). It was previously nested under `infrastructure/ci-cd/.github/workflows/`, a path GitHub Actions never scans, so it never actually ran until it was moved to the repo root.

`.github/workflows/claude.yaml` runs Claude Code on demand: comment `@claude ...` on a PR (review comment, or PR review body) to trigger it. Requires an `ANTHROPIC_API_KEY` repository secret.

## Testing conventions

- Service-layer tests use `@ExtendWith(MockitoExtension.class)` with `@Mock`/`@InjectMocks` on the repository (see `MovieServiceTest`, `FeedbackServiceTest`) — no Spring context loaded.
- Controller-layer tests use `@WebMvcTest(XController.class)` + `MockMvc` + `@MockBean` on the service (see `MovieControllerTest`, `FeedbackControllerTest`), asserting HTTP status codes and JSON body via `jsonPath`.
- api-gateway tests are reactive: `@SpringBootTest(webEnvironment = RANDOM_PORT)` + `WebTestClient` (see `CorsConfigurationTest`). Eureka client and the discovery locator must both be disabled via `@TestPropertySource` (`eureka.client.enabled=false`, `spring.cloud.gateway.discovery.locator.enabled=false`), otherwise context startup fails trying to resolve a `ReactiveDiscoveryClient` that isn't there in tests.
- Each service pom has `jacoco-maven-plugin` wired to the `test` phase for coverage reports (`target/site/jacoco`).

## Known gaps (intentional, not oversights)

- No authentication/authorization anywhere — admin vs. visitor is a UI-only distinction (any client can hit any endpoint).
- `docs/ARCHITECTURE.md` documents these as open items under "Recommended Enhancements"; don't treat that file's "Current Implementation" section as authoritative without cross-checking the actual code, since it has previously drifted from reality (e.g. it once claimed basic auth was implemented when it wasn't).
