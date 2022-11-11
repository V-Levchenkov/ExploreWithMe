package ru.practicum.comments.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.comments.model.Comment;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    @Query
    List<Comment> getAllByUserId(Long userId);

    @Query
    List<Comment> getAllByEventId(Long eventId);
}