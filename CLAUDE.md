# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build & Run

```bash
# Run locally (requires MySQL at localhost:3306)
mvn spring-boot:run

# Run all tests
mvn test

# Run tests with coverage report (enforces 80% threshold on services/mappers)
mvn verify

# Run with Docker (starts app + MySQL 8.0)
docker compose up -d
```

Coverage report is generated at `target/site/jacoco/index.html`.

To run a single test class:
```bash
mvn test -Dtest=BookServiceImplTest
```

## Architecture

Layered architecture: **Controller → Service → Repository → Entity → MySQL**

```
src/main/java/com/library/loansystem/
├── Controllers/         # @RestController, HTTP request/response handling
├── Services/            # Business logic, @Transactional; custom validators in Services/Validators/
├── Repositories/        # Spring Data JPA interfaces
├── Entities/            # JPA entities; Enums/ for BookGenre, BookCopyState, LoanStatus
├── Mapper/              # Manual DTO↔Entity conversion (@Component)
├── DTO/Request/         # Input DTOs with Bean Validation annotations
├── DTO/Response/        # Output DTOs
├── Configuration/       # SecurityConfig (JWT + role-based rules), SwaggerConfig
├── Configuration/Filters/ # JwtTokenValidator (filter chain)
├── Exceptions/          # GlobalExceptionHandler (@RestControllerAdvice), custom exceptions
├── Utils/               # JwtUtils (Auth0 java-jwt)
└── Initializer/         # DataLoader seeds sample data on startup
```

## Security

JWT-based stateless auth. Flow: `POST /auth/log-in` → JWT → `Authorization: Bearer <token>` on subsequent requests. The `JwtTokenValidator` filter validates every request.

Role hierarchy: `ADMIN > LIBRARIAN > USER`
- Public: auth endpoints, Swagger, GET on books/authors/publishers
- LIBRARIAN/ADMIN: catalog mutations (POST/PUT/PATCH on books, copies)
- USER+: loan creation, renewal, personal loan history
- ADMIN only: user management, role assignment

## Key Domain Rules (enforced in Services/Validators/)

- **LoanValidator**: checks book copy availability, user has no overdue loans, due date constraints
- **UserValidator**: prevents deactivation if user has active loans
- A `BookCopy` tracks physical state (Available/Damaged/Lost/Reserved); loans operate on copies, not books
- `AuthorXBook` is the explicit many-to-many join entity between `Author` and `Book`

## Testing Conventions

- Tests use JUnit 5 + Mockito (`@ExtendWith(MockitoExtension.class)`)
- `@Mock` for dependencies, `@InjectMocks` for the unit under test
- `DataProvider.java` in test sources provides shared mock objects
- JaCoCo enforces **80% line coverage** on `/service/*` and `/mapper/*` packages; controllers and config are excluded

## Database

Active profile: `dev` (`application-dev.properties`) — uses `ddl-auto=create-drop` (schema is recreated on every startup). Switch to `prod` profile for `update` mode with env-var-based credentials.

Swagger UI available at `http://localhost:8080/swagger-ui.html` when running locally.