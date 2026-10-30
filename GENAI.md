# Generative AI Tools — Pokémon API

## The Prompt I Used

I used **Claude Code** (Anthropic's official CLI for Claude) as my primary GenAI tool throughout this project. The following prompt captures how I drove the initial scaffolding of the Pokémon API:

```
Build a RESTful Pokémon API in Java 21 with Spring Boot 4 following Clean Architecture.

Requirements:
- CRUD endpoints for tasks (title, description, status enum [TODO/IN_PROGRESS/DONE], due_date)
- Each task belongs to a User (User entity already exists with id, username, email)
- JWT authentication — POST /api/auth/login returns a token; all task endpoints require Bearer token
- GET /api/tasks lists only the authenticated user's tasks
- Validation: title required (max 100 chars), status must be a valid enum, due_date must be in the future
- Error handling: 404 for missing task, 400 for validation errors, 403 if user tries to modify another user's task
- Use Clean Architecture: domain entities/ports in a pure Java layer, use cases in the application layer, JPA adapters in infrastructure, controllers in presentation

Structure the response as individual files per layer. Include unit tests for all use cases using JUnit 5 + Mockito.
```

---

## Representative Sample Output

Claude Code generated the following domain entity (representative sample):

```java
// domain/entity/Task.java
package com.example.tasks.domain.entity;

import java.time.LocalDate;
import java.util.UUID;

public class Task {
    private UUID id;
    private String title;
    private String description;
    private Status status;
    private LocalDate dueDate;
    private UUID userId;

    public enum Status { TODO, IN_PROGRESS, DONE }

    // constructor, getters, setters...
}
```

And the corresponding use case:

```java
// application/usecase/CreateTaskUseCase.java
@Service
public class CreateTaskUseCase {

    private final TaskRepository taskRepository;

    public CreateTaskUseCase(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public Task execute(CreateTaskRequest request, UUID authenticatedUserId) {
        if (request.dueDate() != null && request.dueDate().isBefore(LocalDate.now())) {
            throw new ValidationException("Due date must be in the future");
        }
        Task task = new Task(
            UUID.randomUUID(),
            request.title(),
            request.description(),
            Task.Status.TODO,
            request.dueDate(),
            authenticatedUserId
        );
        return taskRepository.save(task);
    }
}
```

The controller layer:

```java
@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final CreateTaskUseCase createTaskUseCase;
    private final ListUserTasksUseCase listUserTasksUseCase;
    // ...

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TaskDto create(@Valid @RequestBody CreateTaskRequest request,
                          @AuthenticationPrincipal String username) {
        UUID userId = resolveUserId(username);
        return taskMapper.toDto(createTaskUseCase.execute(request, userId));
    }

    @GetMapping
    public List<TaskDto> list(@AuthenticationPrincipal String username) {
        return listUserTasksUseCase.execute(resolveUserId(username))
            .stream().map(taskMapper::toDto).toList();
    }
}
```

---

## How I Validated the AI's Suggestions

### 1. Architecture review
The AI correctly separated concerns across layers but initially placed validation logic inside the JPA entity — a common mistake that violates Clean Architecture's rule that domain entities should not depend on framework concerns. I corrected this by moving validation to the use case layer where it belongs, making it independently testable without Spring context.

### 2. Compilation and type safety
I ran `mvn compile` immediately on generated code. The AI occasionally used Spring Boot 3.x APIs when I was targeting Spring Boot 4.x (e.g., `@MockBean` instead of `@MockitoBean`, `@WebMvcTest` which was removed in Boot 4.0). I corrected these by replacing with `MockMvcBuilders.standaloneSetup()` for controller tests.

### 3. Security gap caught and fixed
The AI's initial `GET /api/tasks` implementation didn't filter by the authenticated user — it listed **all** tasks. This is a critical authorization bug. I added the `authenticatedUserId` parameter extracted from the JWT `SecurityContextHolder` and passed it to the use case, which filtered results in the repository query:

```java
// corrected repository query
List<Task> findByUserId(UUID userId);
```

### 4. Missing edge cases I added
- **Future date validation**: AI generated `dueDate != null` check but didn't validate the date was in the future. Added `dueDate.isBefore(LocalDate.now())` guard.
- **Ownership check on update/delete**: AI generated `PUT /api/tasks/{id}` without verifying the task belonged to the requesting user. Added 403 response:
  ```java
  if (!task.getUserId().equals(authenticatedUserId)) {
      throw new ForbiddenException("You cannot modify another user's task");
  }
  ```
- **Enum deserialization**: AI used `String` for status in the DTO, which allowed any value through. Switched to `@JsonCreator` with proper enum mapping to return 400 on invalid status values.

### 5. Test coverage verification
The AI generated tests but some mocked at too high a level (testing the controller calling the mock service instead of testing the actual business rule). I refactored unit tests to target use cases in isolation, ensuring each test verifies a specific business invariant rather than just "the method was called."

---

## Key Takeaways on AI-Assisted Development

**What AI does well:**
- Scaffolding boilerplate (DTOs, mappers, standard CRUD patterns) extremely fast
- Applying known patterns (Clean Architecture, JWT setup, Flyway migrations) consistently
- Writing comprehensive test stubs that I can refine

**Where human review is essential:**
- Authorization logic (who owns what data) — AI often misses multi-tenancy concerns
- API versioning and backwards compatibility decisions
- Performance implications (N+1 queries, missing indexes) — AI rarely adds `@Index` annotations
- Framework version compatibility — AI training data lags behind latest releases
- Business rule edge cases specific to the domain

**My workflow with Claude Code:**
I used plan mode first to align on architecture before any code generation. This prevented the most common AI mistake: generating code that works locally but violates the intended architectural boundaries. I treated AI output as a *first draft* to review and refine, not a final answer.
