package net.rgielen.todo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.UUID;

public interface TodoRepository extends JpaRepository<Todo, UUID> {
    @Query("SELECT t FROM Todo t WHERE t.completed = :completed")
    Page<Todo> findAllByCompletionStatus(boolean completed, Pageable pageable);
}
