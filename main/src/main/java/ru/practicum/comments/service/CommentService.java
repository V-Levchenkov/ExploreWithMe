package ru.practicum.comments.service;


import org.springframework.transaction.annotation.Transactional;
import ru.practicum.comments.dto.CommentDto;
import ru.practicum.comments.dto.CommentDtoForUpdate;
import ru.practicum.comments.model.Comment;

public interface CommentService {

    @Transactional
    Comment addComment(Comment comment, Long userId, Long eventId);

    CommentDto getComment(Long commentId);

    @Transactional
    void deleteComment(Long commentId, Long userId);

    @Transactional
    CommentDto updateComment(Long userId, Long commentId, CommentDtoForUpdate commentDto);
}