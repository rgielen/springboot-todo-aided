package net.rgielen.todo;

import net.rgielen.TestcontainersConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@Import(TestcontainersConfiguration.class)
class TodoApiIntegrationTest {

    @Autowired
    WebTestClient webTestClient;

    @Autowired
    TodoRepository todoRepository;

    @BeforeEach
    void setUp() {
        todoRepository.deleteAll();
    }

    @Test
    void createTodo_returnsCreatedWithLocationAndBody() {
        webTestClient.post().uri("/api/todos")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {"topic": "Buy groceries", "details": "Milk, eggs, bread"}
                        """)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().exists("Location")
                .expectBody()
                .jsonPath("$.id").isNotEmpty()
                .jsonPath("$.topic").isEqualTo("Buy groceries")
                .jsonPath("$.details").isEqualTo("Milk, eggs, bread")
                .jsonPath("$.completed").isEqualTo(false)
                .jsonPath("$.createdAt").isNotEmpty()
                .jsonPath("$.updatedAt").isNotEmpty();
    }

    @Test
    void createTodo_withoutTopic_returnsBadRequest() {
        webTestClient.post().uri("/api/todos")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {"details": "Some details"}
                        """)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void createTodo_withBlankTopic_returnsBadRequest() {
        webTestClient.post().uri("/api/todos")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {"topic": "   "}
                        """)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void createTodo_withDueDateAndTime() {
        webTestClient.post().uri("/api/todos")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {"topic": "Meeting", "dueDate": "2026-03-15", "dueTime": "14:30:00"}
                        """)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.dueDate").isEqualTo("2026-03-15")
                .jsonPath("$.dueTime").isEqualTo("14:30:00");
    }

    @Test
    void listTodos_returnsPaginatedResults() {
        for (int i = 0; i < 15; i++) {
            todoRepository.save(Todo.builder().topic("Todo " + i).build());
        }

        webTestClient.get().uri("/api/todos?page=0&size=10&sort=topic,asc")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.content.length()").isEqualTo(10)
                .jsonPath("$.totalElements").isEqualTo(15)
                .jsonPath("$.totalPages").isEqualTo(2);
    }

    @Test
    void listTodos_emptyDatabase_returnsEmptyPage() {
        webTestClient.get().uri("/api/todos")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.content.length()").isEqualTo(0)
                .jsonPath("$.totalElements").isEqualTo(0);
    }

    @Test
    void getTodo_existingId_returnsTodo() {
        var todo = todoRepository.save(Todo.builder().topic("Existing").build());

        webTestClient.get().uri("/api/todos/{id}", todo.getId())
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(todo.getId().toString())
                .jsonPath("$.topic").isEqualTo("Existing");
    }

    @Test
    void getTodo_unknownId_returnsNotFound() {
        webTestClient.get().uri("/api/todos/{id}", UUID.randomUUID())
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void updateTodo_existingId_returnsUpdatedTodo() {
        var todo = todoRepository.save(Todo.builder().topic("Original").build());

        webTestClient.put().uri("/api/todos/{id}", todo.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {"topic": "Updated", "completed": true, "dueDate": "2026-06-01"}
                        """)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.topic").isEqualTo("Updated")
                .jsonPath("$.completed").isEqualTo(true)
                .jsonPath("$.dueDate").isEqualTo("2026-06-01");
    }

    @Test
    void updateTodo_unknownId_returnsNotFound() {
        webTestClient.put().uri("/api/todos/{id}", UUID.randomUUID())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {"topic": "Does not matter"}
                        """)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void updateTodo_withoutTopic_returnsBadRequest() {
        var todo = todoRepository.save(Todo.builder().topic("Original").build());

        webTestClient.put().uri("/api/todos/{id}", todo.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {"details": "No topic"}
                        """)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void deleteTodo_existingId_returnsNoContent() {
        var todo = todoRepository.save(Todo.builder().topic("To delete").build());

        webTestClient.delete().uri("/api/todos/{id}", todo.getId())
                .exchange()
                .expectStatus().isNoContent();

        assertThat(todoRepository.findById(todo.getId())).isEmpty();
    }

    @Test
    void deleteTodo_unknownId_returnsNotFound() {
        webTestClient.delete().uri("/api/todos/{id}", UUID.randomUUID())
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void listTodos_filterByCompleted_returnsOnlyCompletedTodos() {
        todoRepository.save(Todo.builder().topic("Todo 1").completed(false).build());
        todoRepository.save(Todo.builder().topic("Todo 2").completed(false).build());
        todoRepository.save(Todo.builder().topic("Todo 3").completed(true).build());

        webTestClient.get().uri("/api/todos?completed=true")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.content.length()").isEqualTo(1)
                .jsonPath("$.totalElements").isEqualTo(1)
                .jsonPath("$.content[0].topic").isEqualTo("Todo 3");
    }

    @Test
    void listTodos_filterByIncomplete_returnsOnlyIncompleteTodos() {
        todoRepository.save(Todo.builder().topic("Todo 1").completed(false).build());
        todoRepository.save(Todo.builder().topic("Todo 2").completed(false).build());
        todoRepository.save(Todo.builder().topic("Todo 3").completed(true).build());

        webTestClient.get().uri("/api/todos?completed=false")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.content.length()").isEqualTo(2)
                .jsonPath("$.totalElements").isEqualTo(2);
    }

    @Test
    void listTodos_filterByCompleted_withPagination() {
        for (int i = 0; i < 5; i++) {
            todoRepository.save(Todo.builder().topic("Incomplete " + i).completed(false).build());
        }
        for (int i = 0; i < 3; i++) {
            todoRepository.save(Todo.builder().topic("Completed " + i).completed(true).build());
        }

        webTestClient.get().uri("/api/todos?completed=false&page=0&size=2&sort=topic,asc")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.content.length()").isEqualTo(2)
                .jsonPath("$.totalElements").isEqualTo(5)
                .jsonPath("$.totalPages").isEqualTo(3);
    }
}
