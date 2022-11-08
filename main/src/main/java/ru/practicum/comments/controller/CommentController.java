package ru.practicum.comments.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.comments.dto.CommentDto;
import ru.practicum.comments.dto.CommentDtoForUpdate;
import ru.practicum.comments.model.Comment;
import ru.practicum.comments.service.CommentService;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/users/{userId}/events/comments")
public class CommentController {
    private final CommentService commentService;

    @PostMapping("{eventId}")
    public Comment addComment(@RequestBody Comment comment, @PathVariable Long userId, @PathVariable Long eventId) {
        log.info("Добавление комментария {}",comment);
        return commentService.addComment(comment, userId, eventId);
    }

    @GetMapping("{commentId}")
    public CommentDto getComment(@PathVariable Long commentId, @PathVariable Long userId) {
        log.info("Удаление комментария с id: {} пользователем с id: {}", commentId, userId);
        return commentService.getComment(commentId);
    }

    @DeleteMapping("{commentId}")
    public void deleteComment(@PathVariable Long commentId, @PathVariable Long userId) {
        log.info("Удаление комментария с id: {}", commentId);
        commentService.deleteComment(commentId, userId);
    }

    @PatchMapping("{commentId}")
    public CommentDto updateComment(@RequestBody CommentDtoForUpdate commentDto,
                                    @PathVariable Long commentId, @PathVariable Long userId) {
        log.info("Изменение комментария с id: {}", commentId);
        return commentService.updateComment(userId, commentId, commentDto);
    }
}