# Book Loan System

## Overview
Book Loan System is a backend application built with **Java and Spring Boot** to manage books, authors, publishers, and book loans.  
The system includes **robust DTO validations**, **centralized exception handling**, and is fully prepared for **Swagger documentation** and **unit test coverage with JaCoCo**.

---

## Technologies
- **Java 17**
- **Spring Boot 3**
- **Maven**
- **Spring Data JPA / MySQL**
- **Bean Validation** (`@Valid`, `@NotBlank`, `@Size`, `@NotNull`, `@Positive`)
- **Swagger / OpenAPI** for API documentation
- **JaCoCo** for test coverage
- **JUnit 5 & Mockito** for unit testing
---
## Project Structure

- 📦 **Controllers** → REST API endpoints
- ⚙️ **Services** → Business logic
- 🔄 **Mapper** → Convert DTO ↔ Entity
- 📝 **DTO** → Request/Response objects with **field validations**
- 🏛️ **Entities** → JPA entities (database tables)
- 🗃️ **Repositories** → Spring Data repositories
- ❗ **Exceptions** → Custom exceptions, handled by **GlobalExceptionHandler**
- ⚙️ **Configuration** → App settings (Swagger, Security, etc.)
- 🚀 **Initializer** → DB setup & sample data
- 🛠️ **Utils** -> Contains JwtUtils

**Extras:**
- **GlobalExceptionHandler** ensures consistent error responses
- **JaCoCo** coverage on services & mappers, target **80%+**
  
---
###  Security & Authentication 🔒
The system uses Spring Security 6 and JWT to provide a secure, stateless authentication layer.

Key Implementation
*JWT Stateless Auth*: Tokens are generated upon login and validated via a Custom Security Filter in every request.

*Role-Based Access Control (RBAC)*: Specific permissions for USER, ADMIN and LIBRARIAN roles to protect sensitive operations.

*Password Safety*: Industry-standard encryption using BCryptPasswordEncoder.

*JWT Provider (Utils)*: Centralized logic for token generation, signature validation, and expiration handling.

*Secure Header*: Protected resources require the Authorization: Bearer <token> header.

---
### Testing & Coverage

Unit tests cover services and mappers using JUnit 5 and Mockito.

Minimal integration tests for REST endpoints.

Coverage reports are generated with JaCoCo.

---
### Documentation

Swagger/OpenAPI is integrated:
Visit: http://localhost:8080/swagger-ui.html

🚀 KEY API Endpoints
💡 Full Documentation: For a complete list of all endpoints, parameters, and schemas, please refer to the Swagger UI at: http://localhost:8080/swagger-ui.html (with the app running).

🔐 Authentication & Security
POST /auth/sign-up -> Register a new account.

POST /auth/log-in -> Authenticate and receive a JWT Token.

👥 User Management
GET /users -> List all users.

GET /users/search?username={val} -> Search users by username (case-insensitive).

POST /users -> Create a new user with manual validation.

PUT /users/{id} -> Update existing user profile.

PATCH /users/{id}/deactivate -> Deactivate user account (Logic: checks for active loans).

PATCH /users/{id}/roles -> [Admin Only] Update user permissions and roles.

📚 Books (Catalog)
GET /books -> List all books in the catalog.

GET /books/isbn/{isbn} -> Find book details by its unique ISBN.

POST /books -> Add a new book title to the system.

PATCH /books/{id}/STOCK -> Manually adjust the global stock of a title.

📖 Book Copies (Inventory)
GET /book-copies/book/{isbn} -> List all physical units/copies of a specific book.

GET /book-copies/available/{isbn} -> Count units currently available for loan.

POST /book-copies -> Register a new physical copy/unit.

PATCH /book-copies/{id}/state -> Update copy condition (Available, Damaged, Lost, etc.).

🤝 Loans (Core Business Logic)
GET /loans -> List all loans (Filterable by: active, overdue, returned).

GET /loans/user/{userId} -> Retrieve loan history for a specific user.

POST /loans -> Register a new loan (Updates copy status and validates user).

PATCH /loans/{id}/return -> Process a return (Makes the copy available again).

PATCH /loans/{id}/renew -> Extend the loan due date based on business rules.

---

### 🛠️ Requirements & Configuration

Java Version: 17+ (Required for Records & Spring Boot 3).
Database: MySQL 8.0+.
License: MIT License.

Environment Profiles
The project includes two configuration files:

application-dev.properties: Pre-configured with hardcoded credentials for local development and immediate testing.

application.properties: Main configuration prepared for production environments.

Security Best Practices
For production deployments, credentials must be loaded via Environment Variables to avoid exposing sensitive data:

DB_USERNAME / DB_PASSWORD: Database credentials.

JWT_SECRET: Secure key for token signing.

### How to Run

1_Clone the repository:

    git clone <repo-url>

2_Configure your database in application.properties.

3_ Run the application with Maven:

    mvn spring-boot:run

4_Run tests and generate coverage:

    mvn verify


