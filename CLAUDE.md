# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Kommunikationsstil

- Kompakte, präzise Anweisungen für Erkenntnisse, Regeln und Pläne
- Keine unnötigen Details — Fokus auf das Wesentliche
- Prägnante, zielgerichtete Sprache ohne Ausschmückungen

## Git & GitHub

- **GitHub CLI (`gh`)** bevorzugen, nicht MCP-Integration
- **Rebase** statt Merge-Commits
- **Kein Squashing** — sinnvolle Zwischencommits sind erwünscht, um abgeschlossene Teilschritte festzuhalten
- Commit-Messages auf **Englisch**:
  - Summary beginnt mit **Verb** (kein Präfix wie `feat:`, `fix:` etc.)
  - Saubere Trennung zwischen Summary und Details
  - So kurz wie möglich, aber vollständig nachvollziehbar

## Project Overview

Spring Boot 3.5.11 application for managing todos, using Java 25, PostgreSQL, and Maven. The project uses both traditional Spring Web (MVC) and Spring WebFlux (reactive) capabilities.

## Build and Run Commands

### Building
```bash
./mvnw clean install      # Full build with tests
./mvnw package            # Package without clean
./mvnw clean package -DskipTests  # Build without running tests
```

### Running the Application
```bash
./mvnw spring-boot:run    # Run with Docker Compose (auto-starts PostgreSQL)
```

Alternatively, run the main class directly in your IDE: `SpringbootTodoAidedApplication`

For development with Testcontainers (instead of Docker Compose), run: `TestSpringbootTodoAidedApplication`

### Testing
```bash
./mvnw test                              # Run all tests
./mvnw test -Dtest=ClassName             # Run specific test class
./mvnw test -Dtest=ClassName#methodName  # Run specific test method
```

## Architecture

### Database Management
- **PostgreSQL** as the primary database
- **Flyway** for database schema migrations (place SQL files in `src/main/resources/db/migration/`)
  - Naming convention: `V{version}__{description}.sql` (e.g., `V1__create_todo_table.sql`)
- **Docker Compose** provides PostgreSQL for local development (see `compose.yaml`)
- **Testcontainers** provides PostgreSQL instances for integration tests

### Development Modes
The project supports two ways to run with a database:

1. **Docker Compose Mode** (default): Running the app via `./mvnw spring-boot:run` automatically starts PostgreSQL via Docker Compose thanks to `spring-boot-docker-compose` dependency
2. **Testcontainers Mode**: Running `TestSpringbootTodoAidedApplication` uses Testcontainers to provision PostgreSQL

### Testing Strategy
- **Test-First**: Immer zuerst Tests schreiben, dann Implementierung
- **Tests sind unveränderlich**: Bei fehlschlagenden Tests wird ausschließlich die Implementierung angepasst, niemals der Test
- **Integrationstests**: `WebTestClient` verwenden für HTTP-basierte Tests
- Testcontainers stellt echte PostgreSQL-Instanzen bereit
- `TestcontainersConfiguration` liefert `@ServiceConnection` PostgreSQL-Container

### Key Dependencies
- **Spring Data JPA**: Database access with JPA/Hibernate
- **Spring Web**: RESTful endpoints (traditional servlet-based)
- **Spring WebFlux**: Reactive web support (can coexist with Spring Web)
- **Lombok**: Reduces boilerplate code (annotation processing configured in Maven compiler plugin)
- **DevTools**: Automatic restart on code changes during development

## Package Structure
Base package: `net.rgielen`

Organize code by feature/domain (e.g., `net.rgielen.todo`, `net.rgielen.user`) rather than by layer (avoid generic `controller`, `service`, `repository` packages at the root).

## Database Configuration
- Development: PostgreSQL via Docker Compose (credentials in `compose.yaml`)
  - Database: `mydatabase`
  - User: `myuser`
  - Password: `secret`
  - Port: 5432 (auto-mapped)
- Tests: PostgreSQL via Testcontainers (ephemeral instances)
