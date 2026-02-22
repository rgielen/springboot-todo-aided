# Copilot Code Review Instructions

## Project Context

Spring Boot 3.5 application (Java 25, PostgreSQL, Maven) for managing todos.

## Review Focus

- **Spring Boot best practices**: Correct use of annotations, dependency injection, configuration
- **JPA/Hibernate**: Entity mappings, query efficiency, N+1 problems, transaction boundaries
- **Security**: SQL injection, input validation, exposed credentials, OWASP top 10
- **Database migrations**: Flyway migration correctness and naming conventions
- **Testing**: Testcontainers usage, test coverage for new code, WebTestClient for HTTP tests
- **Reactive correctness**: Proper use of WebFlux where applicable, no blocking calls in reactive chains

## Conventions

- Package-by-feature structure (e.g., `net.rgielen.todo`), not package-by-layer
- Lombok for boilerplate reduction
- PostgreSQL-specific SQL is acceptable in Flyway migrations
