# Todo REST API

Spring Boot 3.5 Anwendung zur Verwaltung von Todos mit PostgreSQL-Persistenz.

## Tech-Stack

- **Java 25**, **Spring Boot 3.5.11**
- **Spring Data JPA** + **PostgreSQL**
- **Flyway** (Schema-Migrationen)
- **Bean Validation** (Jakarta)
- **Lombok**
- **Testcontainers** (Integrationstests)

## Voraussetzungen

- Java 25+
- Docker (fuer PostgreSQL via Docker Compose oder Testcontainers)

## Starten

```bash
./mvnw spring-boot:run
```

PostgreSQL wird automatisch via Docker Compose gestartet.

Alternativ mit Testcontainers:

```bash
# Main-Klasse: TestSpringbootTodoAidedApplication
./mvnw spring-boot:test-run
```

## Tests

```bash
./mvnw test                              # Alle Tests
./mvnw test -Dtest=TodoApiIntegrationTest  # Nur API-Tests
```

## API

Base-URL: `http://localhost:8080/api/todos`

| Methode | Pfad | Beschreibung | Status |
|---------|------|-------------|--------|
| `POST` | `/api/todos` | Todo erstellen | `201` + Location |
| `GET` | `/api/todos` | Todos auflisten (paginiert) | `200` |
| `GET` | `/api/todos/{id}` | Einzelnes Todo | `200` / `404` |
| `PUT` | `/api/todos/{id}` | Todo aktualisieren | `200` / `404` |
| `DELETE` | `/api/todos/{id}` | Todo loeschen | `204` / `404` |

### Beispiele

```bash
# Todo erstellen
curl -X POST localhost:8080/api/todos \
  -H 'Content-Type: application/json' \
  -d '{"topic": "Einkaufen", "details": "Milch, Eier", "dueDate": "2026-03-01"}'

# Alle Todos (Seite 1, 10 Eintraege, sortiert nach Erstelldatum)
curl 'localhost:8080/api/todos?page=0&size=10&sort=createdAt,desc'

# Todo aktualisieren
curl -X PUT localhost:8080/api/todos/{id} \
  -H 'Content-Type: application/json' \
  -d '{"topic": "Einkaufen", "completed": true}'

# Todo loeschen
curl -X DELETE localhost:8080/api/todos/{id}
```

### Todo-Felder

| Feld | Typ | Pflicht | Beschreibung |
|------|-----|---------|-------------|
| `id` | UUID | auto | Primaerschluessel |
| `topic` | String | ja | Betreffzeile |
| `details` | String | nein | Optionale Details |
| `dueDate` | LocalDate | nein | Faelligkeitsdatum (`YYYY-MM-DD`) |
| `dueTime` | LocalTime | nein | Faelligkeitszeit (`HH:mm:ss`) |
| `completed` | boolean | nein | Default: `false` |
| `createdAt` | Instant | auto | Erstellzeitpunkt |
| `updatedAt` | Instant | auto | Letztes Update |

## Projektstruktur

```
src/main/java/net/rgielen/
  todo/
    Todo.java              # JPA Entity
    TodoRepository.java    # Spring Data Repository
    TodoController.java    # REST Controller
    TodoRequest.java       # Request-DTO mit Validierung

src/main/resources/
  db/migration/
    V1__create_todo_table.sql   # Flyway-Migration
```

## Build

```bash
./mvnw clean install          # Build mit Tests
./mvnw clean package -DskipTests  # Build ohne Tests
```
