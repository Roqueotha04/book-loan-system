# 📚 Book Loan System

Overview

Book Loan System is a backend application built with Java and Spring Boot to manage books, authors, publishers, and loans efficiently. It features DTO validations, centralized exception handling, and role-based access control with secure JWT authentication, ensuring reliability and security for library operations.

Fully documented with Swagger/OpenAPI and covered with unit tests and JaCoCo reports, the system is production-ready, scalable, and designed to handle real-world scenarios, including inventory management, loan processing, and business rule enforcement.

---

## Technologies
-- **Java 17**  
- **Spring Boot 3**  
- **Maven**  
- **Spring Data JPA / MySQL**  
- **Spring Security 6**
- **JWT** for authentication and role-based access control  
- **Bean Validation** (`@Valid`, `@NotBlank`, `@Size`, `@NotNull`, `@Positive`)  
- **Global Exception Handling** (`@ControllerAdvice`)   
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

## 🔒 Security & Authentication

The system uses **Spring Security 6** and **JWT** to provide a secure, stateless authentication layer.

**Key Implementation:**
- **JWT Stateless Auth:** Tokens are generated upon login and validated via a custom security filter on every request.
- **Role-Based Access Control (RBAC):** Specific permissions for `USER`, `ADMIN`, and `LIBRARIAN` roles to protect sensitive operations.
- **Password Safety:** Industry-standard encryption using `BCryptPasswordEncoder`.
- **JWT Provider (Utils):** Centralized logic for token generation, signature validation, and expiration handling.
- **Secure Header:** Protected resources require the `Authorization: Bearer <token>` header.

---

## 🧪 Testing & Coverage

- **Unit tests** cover services and mappers using **JUnit 5** and **Mockito**.
- **Minimal integration tests** for REST endpoints.
- **Coverage reports** are generated with **JaCoCo**.

---

## 📄 Documentation

**Swagger/OpenAPI** is integrated for easy exploration of endpoints.

- **Swagger UI:** [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

### 🚀 Key API Endpoints

#### 🔐 Authentication & Security
| Method | Endpoint | Description |
|--------|---------|-------------|
| POST | `/auth/sign-up` | Register a new account |
| POST | `/auth/log-in` | Authenticate and receive a JWT token |

#### 👥 User Management
| Method | Endpoint | Description |
|--------|---------|-------------|
| GET | `/users` | List all users |
| GET | `/users/search?username={val}` | Search users by username (case-insensitive) |
| POST | `/users` | Create a new user with manual validation |
| PUT | `/users/{id}` | Update existing user profile |
| PATCH | `/users/{id}/deactivate` | Deactivate user account (checks for active loans) |
| PATCH | `/users/{id}/roles` | **[Admin Only]** Update user permissions and roles |

#### 📚 Books (Catalog)
| Method | Endpoint | Description |
|--------|---------|-------------|
| GET | `/books` | List all books |
| GET | `/books/isbn/{isbn}` | Find book details by ISBN |
| POST | `/books` | Add a new book |
| PATCH | `/books/{id}/STOCK` | Adjust the global stock of a title |

#### 📖 Book Copies (Inventory)
| Method | Endpoint | Description |
|--------|---------|-------------|
| GET | `/book-copies/book/{isbn}` | List all copies of a specific book |
| GET | `/book-copies/available/{isbn}` | Count available copies |
| POST | `/book-copies` | Register a new physical copy/unit |
| PATCH | `/book-copies/{id}/state` | Update copy condition (Available, Damaged, Lost, etc.) |

#### 🤝 Loans (Core Business Logic)
| Method | Endpoint | Description |
|--------|---------|-------------|
| GET | `/loans` | List all loans (filterable by active, overdue, returned) |
| GET | `/loans/user/{userId}` | Retrieve loan history for a user |
| POST | `/loans` | Register a new loan (updates copy status and validates user) |
| PATCH | `/loans/{id}/return` | Process a return (makes copy available again) |
| PATCH | `/loans/{id}/renew` | Extend the loan due date according to rules |

---

## 🛠️ Requirements & Configuration

- **Java Version:** 17+ (Required for Records & Spring Boot 3)
- **Database:** MySQL 8.0+
- **License:** MIT License

**Environment Profiles:**
- `application-dev.properties`: Hardcoded credentials for local development.
- `application.properties`: Production-ready configuration.

**Security Best Practices:**  
For production, load credentials via environment variables to avoid exposing sensitive data:

- `DB_USERNAME` / `DB_PASSWORD` → Database credentials
- `JWT_SECRET` → Secure key for token signing


### How to Run in local

1_Clone the repository:

    git clone <repo-url>

2_Configure your database in application.properties and .env variables

3_ Run the application with Maven:

    mvn spring-boot:run

4_Run tests and generate coverage:

    mvn verify


## You can also run the project using Docker

1_Clone the repository:

    git clone <repo-url>

2_Configure your database in application.properties and .env variables

3_Run with docker 

    docker compose up -d
