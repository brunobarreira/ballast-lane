# CLAUDE.md — Ballast Lane Pokémon API

Full-stack project: **Spring Boot 4** (Java 21) backend + **React 18** (TypeScript) frontend.  
Monorepo layout: `backend/`, `frontend/`, `e2e/`, `docker-compose.yml`.

---

## Commands

### Docker (recommended — runs everything)

```bash
docker compose up --build          # build and start all services (first run)
docker compose up --build -d       # same, detached (background)
docker compose down                # stop containers, keep database
docker compose down -v             # stop containers and wipe database volume
docker compose logs -f backend     # tail backend logs
docker compose logs -f             # tail all logs
```

### Backend

```bash
cd backend
./mvnw spring-boot:run             # start backend (port 8080)
./mvnw test                        # run all unit tests
./mvnw test -pl . -Dtest=<Class>   # run a single test class
./mvnw clean package               # clean build + produce JAR
./mvnw clean                       # clean target/ directory
```

### Frontend

```bash
cd frontend
npm install                        # install dependencies
npm run dev                        # start dev server (port 5173)
npm run build                      # production build → dist/
npm run preview                    # preview production build locally
npm run lint                       # run ESLint
```

### E2E Tests (Playwright)

Requires the full stack running on `:3000` / `:8080`.

```bash
cd e2e
npm install
npx playwright install chrome      # first time only
npx playwright test                # run all 18 tests
npx playwright test --headed       # run with visible browser
npx playwright test --ui           # interactive UI mode
npx playwright show-report         # open HTML report after a run
```

---

## Project Structure

### Backend — `backend/src/main/java/com/ballastlane/pokemon/`

Follows **Clean Architecture**: outer layers depend on inner layers, never the reverse.

```
PokemonApiApplication.java              ← Spring Boot entry point

domain/                                 ← Pure Java, zero framework dependencies
  entity/
    Pokemon.java                        ← Local Pokémon domain entity
    User.java                           ← User domain entity
  port/
    PokeApiPort.java                    ← Interface for external Pokémon API calls
  repository/
    PokemonRepository.java              ← Interface for local Pokémon persistence
    UserRepository.java                 ← Interface for user persistence

application/                            ← Use cases; depends only on domain
  usecase/
    ListPokemonUseCase.java             ← US01: paginated list from PokéAPI (cached)
    GetPokemonDetailUseCase.java        ← US02: full detail with stats and evolution
    SyncPokemonUseCase.java             ← US03: fetch from PokéAPI and upsert to DB
    UpdateLocalPokemonUseCase.java      ← US04: update localName, region, tags
    GetLocalPokemonUseCase.java         ← list all locally synced Pokémon
    DeleteLocalPokemonUseCase.java      ← delete a local Pokémon by id
    RegisterUserUseCase.java            ← register a new user account
  dto/
    PokemonListItemDto.java             ← sprite, category, mass, skills
    PokemonDetailDto.java               ← stats map, description, evolution chain
    LocalPokemonDto.java                ← local record with localName, region, tags
    UpdatePokemonRequest.java           ← validated request body for PUT
    AuthRequest.java / AuthResponse.java
    RegisterRequest.java
    PagedResult.java                    ← generic paginated wrapper

infrastructure/                         ← Implements domain ports; framework-aware
  external/pokeapi/
    PokeApiAdapter.java                 ← WebClient calls to pokeapi.co; implements PokeApiPort
  persistence/
    entity/
      PokemonJpaEntity.java             ← JPA entity for pokemon table (JSONB for tags/skills)
      UserJpaEntity.java                ← JPA entity for users table
    mapper/
      PokemonMapper.java                ← domain ↔ JPA entity conversion
      UserMapper.java
    repository/
      JpaPokemonRepository.java         ← implements PokemonRepository
      JpaPokemonSpringRepo.java         ← Spring Data JPA interface
      JpaUserRepository.java            ← implements UserRepository
      JpaUserSpringRepo.java
  security/
    JwtTokenProvider.java               ← generate and validate JWT tokens (JJWT 0.12)
    JwtAuthFilter.java                  ← OncePerRequestFilter: extracts JWT from header
    UserDetailsServiceImpl.java         ← loads user by username for Spring Security
  config/
    SecurityConfig.java                 ← public vs protected routes, stateless session
    WebClientConfig.java                ← WebClient bean + ObjectMapper bean
  cache/
    CacheConfig.java                    ← Caffeine cache, 10-min TTL
  seeder/
    DataSeeder.java                     ← seeds 20 Pokémon + 2 users on startup

presentation/                           ← Controllers and exception handling
  controller/
    PokemonController.java              ← GET /api/pokemon, GET /api/pokemon/{nameOrId}, POST /{nameOrId}/sync
    LocalPokemonController.java         ← GET/PUT/DELETE /api/pokemon/local/{id}
    AuthController.java                 ← POST /api/auth/login, /register
  exception/
    GlobalExceptionHandler.java         ← @ControllerAdvice; RFC 9457 Problem Details
    ResourceNotFoundException.java      ← thrown on 404
    PokeApiException.java               ← thrown on 502 (PokéAPI unreachable)
```

**Resources:**

```
src/main/resources/
  application.yml                       ← DB URL, JWT secret, PokéAPI base URL
  db/migration/
    V1__create_users.sql                ← Flyway: users table
    V2__create_pokemon.sql              ← Flyway: pokemon table

src/test/resources/
  application-test.yml                  ← H2 in-memory DB, Flyway disabled
```

**Tests — `backend/src/test/java/com/ballastlane/pokemon/`:**

```
application/
  ListPokemonUseCaseTest.java           ← unit: pagination, cache key
  GetPokemonDetailUseCaseTest.java      ← unit: stats, evolution chain
  SyncPokemonUseCaseTest.java           ← unit: insert new, upsert existing
  UpdateLocalPokemonUseCaseTest.java    ← unit: 404 on missing, updates fields
  RegisterUserUseCaseTest.java          ← unit: duplicate username throws
presentation/
  PokemonControllerTest.java            ← MockMvc: list and detail endpoints
  LocalPokemonControllerTest.java       ← MockMvc: CRUD + auth enforcement
PokemonApiApplicationTests.java         ← Spring context smoke test (H2 + mocks)
```

---

### Frontend — `frontend/src/`

```
main.tsx                                ← React entry point
App.tsx                                 ← Router, QueryClientProvider, AuthProvider, ProtectedRoute

api/
  client.ts                             ← Axios instance; JWT injected via interceptor
  pokemon.ts                            ← API functions: fetchPokemonList, fetchPokemonDetail, syncPokemon, etc.

contexts/
  AuthContext.tsx                       ← login/logout/isAuthenticated; JWT stored in localStorage

components/
  Navbar.tsx                            ← top nav; shows My Pokémon + Logout when authenticated
  PokemonCard.tsx                       ← card used in the paginated grid (sprite, name, category, mass, skills)

pages/
  PokemonListPage.tsx                   ← US01: paginated grid with Next/Previous
  PokemonDetailPage.tsx                 ← US02: image, stats bars, description, evolution chain, Sync button
  LocalPokemonPage.tsx                  ← US03/04: local collection grid with Edit modal and ConfirmModal
  LoginPage.tsx                         ← login form with inline error
  RegisterPage.tsx                      ← registration form

types/
  index.ts                              ← shared TypeScript interfaces (Pokemon, LocalPokemon, Page, etc.)
```

---

## Conventions

### Backend

- **Package by layer**, not by feature: `domain`, `application`, `infrastructure`, `presentation`
- **Use cases are the API of the application layer** — controllers call use cases, never repositories directly
- **Domain entities have no Spring annotations** — `@Entity`, `@Column` etc. live only in `infrastructure/persistence/entity/`
- **DTOs live in `application/dto/`** — never expose JPA entities from controllers
- **Validation** (`@NotBlank`, `@Size`) on request DTOs in `application/dto/`, enforced via `@Valid` in controllers
- **Error responses** follow RFC 9457 — all exceptions go through `GlobalExceptionHandler`
- **Tests use `application-test` profile** — H2 in-memory, Flyway disabled, `@MockitoBean` for external ports
- **Controller tests** use `MockMvcBuilders.standaloneSetup()` — no Spring context needed

### Frontend

- **One file per page** in `pages/`, one file per reusable component in `components/`
- **All API calls** go through `api/pokemon.ts` — pages never call Axios directly
- **Auth state** lives exclusively in `AuthContext` — components read `isAuthenticated` from context
- **Data fetching** via TanStack Query (`useQuery`, `useMutation`) — no `useEffect` for API calls
- **No `window.alert()` or `window.confirm()`** — use `Toast` (inline, 3 s auto-dismiss) and `ConfirmModal` instead
- **Tailwind utility classes only** — no custom CSS files

### Git

- Commit messages in English, imperative mood ("Add feature", not "Added feature")
- Small, focused commits — one concern per commit
