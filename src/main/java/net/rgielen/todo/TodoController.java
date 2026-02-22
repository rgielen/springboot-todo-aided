package net.rgielen.todo;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/api/todos")
public class TodoController {

    private final TodoRepository repository;

    TodoController(TodoRepository repository) {
        this.repository = repository;
    }

    @PostMapping
    ResponseEntity<Todo> create(@Valid @RequestBody TodoRequest request) {
        var todo = Todo.builder()
                .topic(request.topic())
                .details(request.details())
                .dueDate(request.dueDate())
                .dueTime(request.dueTime())
                .completed(request.completed() != null && request.completed())
                .build();
        var saved = repository.save(todo);
        return ResponseEntity
                .created(URI.create("/api/todos/" + saved.getId()))
                .body(saved);
    }

    @GetMapping
    Page<Todo> list(Pageable pageable) {
        return repository.findAll(pageable);
    }

    @GetMapping("/{id}")
    ResponseEntity<Todo> get(@PathVariable UUID id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    ResponseEntity<Todo> update(@PathVariable UUID id, @Valid @RequestBody TodoRequest request) {
        return repository.findById(id)
                .map(todo -> {
                    todo.setTopic(request.topic());
                    todo.setDetails(request.details());
                    todo.setDueDate(request.dueDate());
                    todo.setDueTime(request.dueTime());
                    if (request.completed() != null) {
                        todo.setCompleted(request.completed());
                    }
                    return ResponseEntity.ok(repository.save(todo));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    ResponseEntity<Void> delete(@PathVariable UUID id) {
        if (!repository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
