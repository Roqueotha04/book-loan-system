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

## Key Endpoints

### Publisher
- `GET /publishers` → List all publishers
- `GET /publishers/{id}` → Get a publisher by ID
- `POST /publishers` → Create a publisher (validated)
- `PUT /publishers/{id}` → Update a publisher
- `DELETE /publishers/{id}` → Delete a publisher

### Author
- `GET /authors` → List all authors
- `GET /authors/{id}` → Get an author by ID
- `POST /authors` → Create an author (validated)
- `PUT /authors/{id}` → Update an author
- `DELETE /authors/{id}` → Delete an author

### Book
- `GET /books` → List all books
- `GET /books/{id}` → Get a book by ID
- `GET /books/isbn/{isbn}` → Get a book by ISBN
- `POST /books` → Create a book (validated, ISBN required)
- `PUT /books/{id}` → Update a book
- `DELETE /books/{id}` → Delete a book

### Loan
- CRUD for loaning books to users.

### User
- CRUD for Users.

Loan and User CRUD are under development, new features will be soon.

> Validation errors are returned as JSON in the format: json
{
"fieldName": "error message"
}       
---

## Project Structure

## Project Structure

- 📦 **Controllers** → REST API endpoints
- ⚙️ **Services** → Business logic
- 🔄 **Mapper** → Convert DTO ↔ Entity
- 📝 **DTO** → Request/Response objects with **field validations**
- 🏛️ **Entities** → JPA entities (database tables)
- 🗃️ **Repositories** → Spring Data repositories
- ❗ **Exceptions** → Custom exceptions, handled by **GlobalExceptionHandler**
- ⚙️ **Configuration** → App settings (Swagger, CORS, etc.)
- 🚀 **Initializer** → DB setup & sample data

**Extras:**
- **GlobalExceptionHandler** ensures consistent error responses
- **JaCoCo** coverage on services & mappers, target **80%+**

### Testing & Coverage

Unit tests cover services and mappers using JUnit 5 and Mockito.

Minimal integration tests for REST endpoints.

Coverage reports are generated with JaCoCo.

Non-core classes (controllers, DTOs, configuration, initializers) are excluded from coverage checks.

---
### Documentation

Swagger/OpenAPI is integrated:
Visit: http://localhost:8080/swagger-ui.html

---
### How to Run

1_Clone the repository:

    git clone <repo-url>
2_Configure your database in application.properties.

3_ Run the application with Maven:

    mvn spring-boot:run
4_Run tests and generate coverage:

    mvn verify



