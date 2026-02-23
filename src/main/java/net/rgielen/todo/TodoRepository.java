package net.rgielen.todo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface TodoRepository extends JpaRepository<Todo, UUID> {
    @Query("SELECT t FROM Todo t WHERE t.completed = :completed")
    Page<Todo> findAllByCompletionStatus(boolean completed, Pageable pageable);

    @Query("SELECT t FROM Todo t ORDER BY t.createdAt ASC")
    List<Todo> findAllOrderByCreatedAtAsc();

    @Query("SELECT t FROM Todo t WHERE t.completed = :completed ORDER BY t.createdAt ASC")
    List<Todo> findAllByCompleted(boolean completed);

    @Query("SELECT COUNT(t) FROM Todo t WHERE t.completed = false")
    long countActive();

    @Modifying
    @Query("UPDATE Todo t SET t.completed = :completed")
    void updateAllCompleted(boolean completed);

    @Modifying
    @Query("DELETE FROM Todo t WHERE t.completed = true")
    void deleteAllCompleted();

    @Query("SELECT CASE WHEN COUNT(t) = 0 THEN false ELSE (COUNT(CASE WHEN t.completed = false THEN 1 END) = 0) END FROM Todo t")
    boolean areAllCompleted();
}
