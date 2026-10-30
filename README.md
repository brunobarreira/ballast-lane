# Ballast Lane — Pokémon API

A full-stack application built with **Spring Boot 4** (Java 21) + **React 18** (TypeScript), following **Clean Architecture** and **TDD** principles. Integrates with the public [PokéAPI](https://pokeapi.co) for data retrieval, persists a local copy to PostgreSQL, and allows trainers to annotate their collection with custom fields.

## Architecture

### Backend — Clean Architecture layers

```
com.ballastlane.pokemon
├── domain/           ← Pure Java. Entities, Repository interfaces, Port interfaces. Zero Spring deps.
├── application/      ← Use cases (interactors), DTOs. Depends only on domain.
├── infrastructure/   ← JPA adapters, PokeAPI WebClient, JWT, Caffeine cache. Implements domain ports.
└── presentation/     ← REST controllers, exception handlers. Depends on application layer.
```

**Why this matters:** the `domain` and `application` packages compile and test without Spring or PostgreSQL. Business logic is framework-independent.

### Frontend — React + TypeScript

| Layer | Technology |
|-------|-----------|
| State / data fetching | TanStack Query (stale-while-revalidate caching) |
| Routing | React Router v6 |
| Auth | JWT stored in localStorage, injected via Axios interceptor |
| Styling | Tailwind CSS v3 (utility-first, responsive) |

---

## User Stories Implemented

| Story | Endpoint | Auth |
|-------|----------|------|
| **US01** Browse Pokémon (paginated, sprite, category, mass, skills) | `GET /api/pokemon?page=0&size=20` | Public |
| **US02** Detailed Pokémon view (image, stats, description, evolution) | `GET /api/pokemon/{nameOrId}` | Public |
| **US03** Sync Pokémon to local database | `POST /api/pokemon/{nameOrId}/sync` | Bearer JWT |
| **US04** Update local Pokémon (local name, region, tags) | `PUT /api/pokemon/local/{id}` | Bearer JWT |
| List local collection | `GET /api/pokemon/local` | Bearer JWT |
| Delete from local | `DELETE /api/pokemon/local/{id}` | Bearer JWT |
| Register | `POST /api/auth/register` | Public |
| Login → JWT | `POST /api/auth/login` | Public |

---

## Quick Start — Docker Compose

**Prerequisites:** Docker + Docker Compose

```bash
git clone <repo-url>
cd ballast-lane
docker compose up
```

| Service | URL |
|---------|-----|
| Frontend | http://localhost:3000 |
| Backend API | http://localhost:8080 |
| PostgreSQL | localhost:5432 |

On first startup, the backend seeds **20 Pokémon** and **2 user accounts** automatically.

### Demo Credentials

| Username | Password | Role |
|----------|----------|------|
| `trainer` | `trainer123` | USER |
| `admin` | `admin123` | ADMIN |

---

## Local Development (without Docker)

### Prerequisites
- Java 21
- Maven 3.9+
- Node 20+
- PostgreSQL 16 running locally

### Backend

```bash
cd backend

# Configure database (or set env vars)
export DATABASE_URL=jdbc:postgresql://localhost:5432/pokemondb
export DATABASE_USER=pokemon
export DATABASE_PASSWORD=pokemon
export JWT_SECRET=ballastlane-pokemon-secret-key-must-be-at-least-32-chars-long

./mvnw spring-boot:run
```

### Frontend

```bash
cd frontend

# Create .env.local if backend runs on different port
echo "VITE_API_URL=http://localhost:8080" > .env.local

npm install
npm run dev
# → http://localhost:5173
```

### Run tests

```bash
cd backend
./mvnw test
```

---

## API Reference

### Authentication

```bash
# Register
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"ash","email":"ash@pokemon.com","password":"pikachu1"}'

# Login → get token
TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"trainer","password":"trainer123"}' | jq -r '.token')
```

### Pokémon (public)

```bash
# Paginated list
curl "http://localhost:8080/api/pokemon?page=0&size=20"

# Detail view
curl "http://localhost:8080/api/pokemon/pikachu"
```

### Local database (requires JWT)

```bash
# Sync a Pokémon to local DB
curl -X POST "http://localhost:8080/api/pokemon/pikachu/sync" \
  -H "Authorization: Bearer $TOKEN"

# List all synced Pokémon
curl "http://localhost:8080/api/pokemon/local" \
  -H "Authorization: Bearer $TOKEN"

# Update local Pokémon (by UUID returned from sync/list)
curl -X PUT "http://localhost:8080/api/pokemon/local/{id}" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"localName":"Pikachu-PT","region":"Kanto","tags":["electric","cute"]}'

# Delete
curl -X DELETE "http://localhost:8080/api/pokemon/local/{id}" \
  -H "Authorization: Bearer $TOKEN"
```

---

## Error Handling

All errors follow [RFC 9457 Problem Details](https://www.rfc-editor.org/rfc/rfc9457):

```json
{
  "type": "about:blank",
  "title": "Resource Not Found",
  "status": 404,
  "detail": "Pokemon not found with id: abc123",
  "instance": "/api/pokemon/local/abc123"
}
```

| Scenario | Status |
|----------|--------|
| Missing local Pokémon | 404 |
| Invalid payload / validation failure | 400 (with `errors` map) |
| Missing / invalid JWT | 401 |
| PokeAPI unreachable | 502 |

---

## Tech Stack

| Layer | Technology |
|-------|-----------|
| Language | Java 21 |
| Framework | Spring Boot 4.0 |
| Security | Spring Security + JJWT 0.12 |
| Database | PostgreSQL 16 |
| Migrations | Flyway |
| ORM | Spring Data JPA / Hibernate |
| HTTP client | Spring WebFlux WebClient |
| Cache | Caffeine (10-minute TTL) |
| Testing | JUnit 5 + Mockito + MockMvc |
| Frontend | React 18 + TypeScript + Vite 5 |
| Styling | Tailwind CSS v3 |
| Data fetching | TanStack Query v5 |
| Containerization | Docker + Docker Compose |

---

## Highlights — Beyond the Requirements

These items were not explicitly required but were added to improve quality and demonstrate best practices:

| Highlight | Detail |
|-----------|--------|
| **Spring Boot 4.0** | Latest major release, requires Spring Framework 7 and Java 21 |
| **Flyway migrations** | Versioned schema (V1/V2) instead of `ddl-auto: create` — production-safe |
| **RFC 9457 Problem Details** | Standard error format with `title`, `status`, `detail` fields |
| **Upsert on sync** | Syncing the same Pokémon twice updates it instead of duplicating |
| **Official artwork sprites** | Detail page uses high-res artwork; list uses the small sprite |
| **Color-coded stat bars** | HP → green, Attack → red, Defense → blue, Speed → yellow, etc. |
| **Clickable evolution chain** | Navigate between evolutions directly from the detail page |
| **TanStack Query** | Client-side cache with `staleTime: 5min` — avoids redundant API calls |
| **Toast notifications** | Inline feedback for sync success/error instead of `window.alert()` |
| **Confirmation modal** | Custom delete confirmation instead of `window.confirm()` |
| **H2 for tests** | Unit and integration tests run without PostgreSQL — CI-friendly |
| **CORS configured** | Allows any origin, ready for separate frontend deployment |
| **Docker health check** | Backend waits for Postgres to be ready before starting |

---

## Thought Process

### Why Clean Architecture?

Clean Architecture solves the testability problem: use case logic can be unit-tested without starting a Spring context or hitting a database. The PokeAPI client is behind a `PokeApiPort` interface — in tests I swap it with a Mockito mock and test the business logic in isolation.

### Why Caffeine cache?

PokeAPI data is stable (Pokémon don't change their base stats). A 10-minute in-memory cache drastically reduces external API calls on the paginated browse page (US01). The cache is keyed by page + size, so pagination works correctly.

### Why PostgreSQL JSONB for tags/skills?

Tags and skills are variable-length lists without their own identity or relationships — they don't warrant a separate join table. JSONB gives us flexible storage while keeping the schema simple. For a production system with tag filtering/search, moving to a proper join table would make sense.

### TDD approach

I wrote use case tests first (red), then implemented the use case (green), then refactored. Controller tests use `MockMvcBuilders.standaloneSetup()` so they run without a Spring context, keeping the feedback loop fast.
