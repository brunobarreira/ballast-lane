# Generative AI Tools — Pokémon API

## The Prompt I Used

I used **Claude Code** (Anthropic's official CLI for Claude) as my primary GenAI tool throughout this project. The following prompt captures how I drove the initial scaffolding:

```
Build a full-stack Pokémon API in Java 21 with Spring Boot 4 and a React 18 + TypeScript frontend,
following Clean Architecture and TDD principles.

Requirements:
- Integrate with the public PokéAPI (https://pokeapi.co) to fetch paginated Pokémon lists and detail views
- Persist a local copy of Pokémon to PostgreSQL; allow authenticated users to annotate with localName, region and tags
- JWT authentication — POST /api/auth/login returns a token; sync/update/delete endpoints require Bearer token
- GET /api/pokemon?page=0&size=20 returns paginated list with sprite, category, mass and skills — cache with Caffeine (10 min TTL)
- GET /api/pokemon/{nameOrId} returns full detail: image, stats, description, evolution chain
- POST /api/pokemon/{nameOrId}/sync fetches from PokéAPI and upserts to local DB
- PUT /api/pokemon/local/{id} updates localName, region and tags (404 if missing, 400 if invalid)
- DELETE /api/pokemon/local/{id} removes the local record
- Use Clean Architecture: domain entities/ports in a pure Java layer, use cases in application layer,
  JPA + WebClient adapters in infrastructure, controllers in presentation
- Frontend: React Router, TanStack Query, Tailwind CSS, protected routes, custom Toast and ConfirmModal components
- Docker Compose with postgres + backend + frontend; seed 20 Pokémon and 2 users on startup

Structure the response as individual files per layer. Include unit tests for all use cases using JUnit 5 + Mockito.
```

---

## Representative Sample Output

Claude Code generated the following domain entity (representative sample):

```java
// domain/entity/Pokemon.java
package com.ballastlane.pokemon.domain.entity;

import java.util.List;
import java.util.UUID;

public class Pokemon {
    private UUID id;
    private int pokeapiId;
    private String name;
    private String localName;
    private String category;
    private int massHg;
    private String spriteUrl;
    private String region;
    private List<String> tags;
    private List<String> skills;

    // constructor, getters, setters...
}
```

And the corresponding use case:

```java
// application/usecase/SyncPokemonUseCase.java
@Service
public class SyncPokemonUseCase {

    private final PokeApiPort pokeApiPort;
    private final PokemonRepository pokemonRepository;

    public SyncPokemonUseCase(PokeApiPort pokeApiPort, PokemonRepository pokemonRepository) {
        this.pokeApiPort = pokeApiPort;
        this.pokemonRepository = pokemonRepository;
    }

    public Pokemon execute(String nameOrId) {
        Pokemon fetched = pokeApiPort.getPokemonListItem(nameOrId);
        return pokemonRepository.findByPokeapiId(fetched.getPokeapiId())
            .map(existing -> {
                existing.setName(fetched.getName());
                existing.setSpriteUrl(fetched.getSpriteUrl());
                existing.setSkills(fetched.getSkills());
                return pokemonRepository.save(existing);
            })
            .orElseGet(() -> pokemonRepository.save(fetched));
    }
}
```

The controller layer:

```java
// presentation/controller/PokemonController.java
@RestController
@RequestMapping("/api/pokemon")
public class PokemonController {

    private final ListPokemonUseCase listPokemonUseCase;
    private final GetPokemonDetailUseCase getDetailUseCase;
    private final SyncPokemonUseCase syncUseCase;

    @GetMapping
    public PokemonPageResponse list(@RequestParam(defaultValue = "0") int page,
                                    @RequestParam(defaultValue = "20") int size) {
        return listPokemonUseCase.execute(page, size);
    }

    @GetMapping("/{nameOrId}")
    public PokemonDetailDto detail(@PathVariable String nameOrId) {
        return getDetailUseCase.execute(nameOrId);
    }

    @PostMapping("/{nameOrId}/sync")
    @ResponseStatus(HttpStatus.CREATED)
    public LocalPokemonDto sync(@PathVariable String nameOrId) {
        return pokemonMapper.toLocalDto(syncUseCase.execute(nameOrId));
    }
}
```

---

## How I Validated the AI's Suggestions

### 1. Architecture review
The AI correctly separated concerns across layers but initially placed validation logic inside the JPA entity — a violation of Clean Architecture's rule that domain entities must not depend on framework concerns. I moved validation to the use case layer, making it independently testable without a Spring context.

### 2. Compilation and type safety
I ran `mvn compile` immediately on generated code. The AI occasionally used Spring Boot 3.x APIs when targeting Spring Boot 4.x (e.g., `@MockBean` instead of `@MockitoBean`, `@WebMvcTest` which was removed in Boot 4.0). I corrected these by replacing controller tests with `MockMvcBuilders.standaloneSetup()`.

### 3. WebClient deserialization bug caught and fixed
The AI's `PokeApiAdapter` used `bodyToMono(JsonNode.class)` which throws a type definition error under Spring Boot 4's WebFlux + Jackson configuration. I fixed it by switching to `bodyToMono(String.class)` and deserializing manually via `objectMapper.readTree(body)`:

```java
// fixed deserialization in PokeApiAdapter
return webClient.get().uri(url)
    .retrieve()
    .bodyToMono(String.class)
    .map(body -> objectMapper.readTree(body));
```

### 4. Missing edge cases I added
- **Upsert on sync**: AI generated a plain `save()` that would duplicate records on repeated sync. Added `findByPokeapiId` lookup to update the existing row instead.
- **H2 compatibility**: AI used `columnDefinition = "jsonb"` and `columnDefinition = "uuid"` in JPA entities, which breaks H2 in tests. Removed both — Hibernate picks the correct types per dialect.
- **Toast instead of `window.alert()`**: AI generated `alert()` calls for sync feedback. Replaced with an inline `Toast` component (3-second auto-dismiss).
- **ConfirmModal instead of `window.confirm()`**: AI generated `confirm()` for delete. Replaced with a custom modal with "Remove" and "Cancel" buttons.

### 5. Test coverage verification
The AI generated tests but some mocked at too high a level (verifying "the method was called" rather than the actual business rule). I refactored unit tests to target use cases in isolation, ensuring each test verifies a specific invariant — e.g., that syncing an already-existing Pokémon updates the record instead of inserting a duplicate.

---

## Key Takeaways on AI-Assisted Development

**What AI does well:**
- Scaffolding boilerplate (DTOs, mappers, standard CRUD patterns) extremely fast
- Applying known patterns (Clean Architecture, JWT setup, Flyway migrations) consistently
- Writing comprehensive test stubs that I can refine

**Where human review is essential:**
- Framework version compatibility — AI training data lags behind latest releases (Boot 3 vs Boot 4 breaking changes)
- Integration edge cases (WebClient + Jackson, H2 vs PostgreSQL dialect differences)
- UI behaviour — AI defaults to native browser dialogs (`alert`, `confirm`) instead of custom components
- Upsert vs insert semantics — AI defaults to simple `save()` without idempotency checks

**My workflow with Claude Code:**
I used plan mode first to align on architecture before any code generation. This prevented the most common AI mistake: generating code that works locally but violates the intended architectural boundaries. I treated AI output as a *first draft* to review and refine, not a final answer.
