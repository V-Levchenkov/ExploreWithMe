package ru.practicum.comments.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.comments.dto.CommentDto;
import ru.practicum.comments.dto.CommentShortDto;
import ru.practicum.comments.model.Comment;
import ru.practicum.comments.service.CommentService;

import java.util.List;


@Slf4j
@RestController
@Validated
@RequestMapping("/admin/comments")
@RequiredArgsConstructor
public class CommentControllerAdmin {
    private final CommentService commentService;

    @GetMapping("/users/{userId}")
    public List<Comment> getAllCommentsByUser(@PathVariable Long userId) {
        return commentService.getAllCommentsByUser(userId);
    }

    @GetMapping("/events/{eventId}")
    public List<Comment> getAllCommentsByEvent(@PathVariable Long eventId) {
        return commentService.getAllCommentsByEvent(eventId);
    }

    @PatchMapping("/{userId}/{commentId}")
    public CommentDto updateComment(@RequestBody CommentShortDto commentDto,
                                    @PathVariable Long commentId, @PathVariable Long userId) {
        log.info("Изменение комментария с id: {}", commentId);
        return commentService.updateComment(userId, commentId, commentDto);
    }
}