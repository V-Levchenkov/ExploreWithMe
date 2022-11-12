package ru.practicum.comments.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.comments.dto.CommentDto;
import ru.practicum.comments.dto.CommentShortDto;
import ru.practicum.comments.model.Comment;
import ru.practicum.comments.service.CommentService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;

@RestController
@RequiredArgsConstructor
@Slf4j
public class CommentControllerPrivate {
    private final CommentService commentService;

    @PostMapping("/users/{userId}/comments/events/{eventId}")
    public Comment addComment(@PathVariable(value = "userId") @Positive Long userId,
                              @PathVariable(value = "eventId") @Positive Long eventId,
                              @RequestBody @Valid CommentShortDto comment) {
        log.info("Добавление комментария {}", comment);
        return commentService.addComment(comment, userId, eventId);
    }

    @GetMapping("comments/{commentId}")
    public CommentDto getComment(@PathVariable Long commentId) {
        log.info("Получение комментария с id: {}", commentId);
        return commentService.getComment(commentId);
    }

    @DeleteMapping("/users/{userId}/comments/{commentId}")
    public void deleteComment(@PathVariable Long commentId, @PathVariable Long userId) {
        log.info("Удаление комментария с id: {}", commentId);
        commentService.deleteComment(commentId, userId);
    }
}