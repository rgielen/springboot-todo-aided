package net.rgielen.todo;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import io.github.wimdeblauwe.htmx.spring.boot.mvc.HxRequest;

import java.util.List;
import java.util.UUID;

@Controller
public class TodoViewController {

    private final TodoRepository repository;

    TodoViewController(TodoRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/")
    String index(Model model) {
        populateModel(model, "all");
        return "index";
    }

    @HxRequest
    @PostMapping("/todos")
    String createTodo(@RequestParam String topic, @RequestParam(defaultValue = "all") String filter, Model model) {
        repository.save(Todo.builder().topic(topic.trim()).build());
        populateModel(model, filter);
        return "fragments/todo-list :: todo-content";
    }

    @HxRequest
    @Transactional
    @PutMapping("/todos/{id}/toggle")
    String toggleTodo(@PathVariable UUID id, @RequestParam(defaultValue = "all") String filter, Model model) {
        repository.findById(id).ifPresent(todo -> {
            todo.setCompleted(!todo.isCompleted());
            repository.save(todo);
        });
        populateModel(model, filter);
        return "fragments/todo-list :: todo-content";
    }

    @HxRequest
    @PutMapping("/todos/{id}")
    String updateTodo(@PathVariable UUID id, @RequestParam String topic, Model model) {
        repository.findById(id).map(t -> {
            t.setTopic(topic.trim());
            return repository.save(t);
        });
        populateModel(model, "all");
        return "fragments/todo-list :: todo-content";
    }

    @HxRequest
    @Transactional
    @DeleteMapping("/todos/{id}")
    String deleteTodo(@PathVariable UUID id, @RequestParam(defaultValue = "all") String filter, Model model) {
        repository.deleteById(id);
        populateModel(model, filter);
        return "fragments/todo-list :: todo-content";
    }

    @HxRequest
    @Transactional
    @PostMapping("/todos/toggle-all")
    String toggleAll(@RequestParam(defaultValue = "all") String filter, Model model) {
        boolean allCompleted = repository.areAllCompleted();
        repository.updateAllCompleted(!allCompleted);
        populateModel(model, filter);
        return "fragments/todo-list :: todo-content";
    }

    @HxRequest
    @Transactional
    @PostMapping("/todos/clear-completed")
    String clearCompleted(@RequestParam(defaultValue = "all") String filter, Model model) {
        repository.deleteAllCompleted();
        populateModel(model, filter);
        return "fragments/todo-list :: todo-content";
    }

    @HxRequest
    @GetMapping("/todos")
    String filterTodos(@RequestParam(defaultValue = "all") String filter, Model model) {
        populateModel(model, filter);
        return "fragments/todo-list :: todo-content";
    }

    private void populateModel(Model model, String filter) {
        List<Todo> todos = switch (filter) {
            case "active" -> repository.findAllByCompleted(false);
            case "completed" -> repository.findAllByCompleted(true);
            default -> repository.findAllOrderByCreatedAtAsc();
        };
        long activeCount = repository.countActive();
        long totalCount = repository.count();
        long completedCount = totalCount - activeCount;
        boolean allCompleted = totalCount > 0 && activeCount == 0;

        model.addAttribute("todos", todos);
        model.addAttribute("activeCount", activeCount);
        model.addAttribute("totalCount", totalCount);
        model.addAttribute("completedCount", completedCount);
        model.addAttribute("allCompleted", allCompleted);
        model.addAttribute("filter", filter);
    }
}
