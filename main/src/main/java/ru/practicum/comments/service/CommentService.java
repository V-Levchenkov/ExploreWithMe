package ru.practicum.comments.service;


import org.springframework.transaction.annotation.Transactional;
import ru.practicum.comments.dto.CommentDto;
import ru.practicum.comments.dto.CommentShortDto;
import ru.practicum.comments.model.Comment;

import java.util.List;

public interface CommentService {

    @Transactional
    Comment addComment(CommentShortDto comment, Long userId, Long eventId);

    CommentDto getComment(Long commentId);

    @Transactional
    List<Comment> getAllCommentsByUser(Long userId);

    @Transactional
    List<Comment> getAllCommentsByEvent(Long eventId);

    @Transactional
    void deleteComment(Long commentId, Long userId);

    @Transactional
    CommentDto updateComment(Long userId, Long commentId, CommentShortDto commentDto);
}