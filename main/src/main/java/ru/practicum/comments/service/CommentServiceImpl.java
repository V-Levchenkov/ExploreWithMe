package ru.practicum.comments.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.comments.dto.CommentDto;
import ru.practicum.comments.dto.CommentMapper;
import ru.practicum.comments.dto.CommentShortDto;
import ru.practicum.comments.model.Comment;
import ru.practicum.comments.storage.CommentRepository;
import ru.practicum.event.model.Event;
import ru.practicum.event.service.EventService;
import ru.practicum.exceptions.NotFoundException;
import ru.practicum.exceptions.WrongOwnerException;
import ru.practicum.users.model.User;
import ru.practicum.users.service.UserService;

import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final EventService eventService;
    private final UserService userService;

    @Override
    @Transactional
    public Comment addComment(CommentShortDto comment, Long userId, Long eventId) {
        User user = userService.getUserById(userId);
        Event event = eventService.getEventByIdPrivate(eventId);
        return commentRepository.save(commentMapper.toComment(comment, user, event));
    }

    @Override
    public CommentDto getComment(Long commentId) {
        Comment comment = commentValidation(commentId);
        log.info("Получен комментарий с id: {}", commentId);
        return commentMapper.toDto(comment);
    }

    @Override
    @Transactional
    public List<Comment> getAllCommentsByUser(Long userId) {
        log.info("Получены комментарии пользователя с id: {}", userId);
        return commentRepository.getAllByUserId(userId);
    }

    @Override
    @Transactional
    public List<Comment> getAllCommentsByEvent(Long eventId) {
        log.info("Получены комментарии мероприятия с id: {}", eventId);
        return commentRepository.getAllByEventId(eventId);
    }

    @Override
    @Transactional
    public void deleteComment(Long commentId, Long userId) {
        commentValidation(commentId);
        commentWithUserValidation(commentId, userId);
        commentRepository.deleteById(commentId);
        log.info("Удален комментарий c id {}", commentId);

    }

    @Override
    @Transactional
    public CommentDto updateComment(Long userId, Long commentId, CommentShortDto commentDto) {
        Comment comment = commentWithUserValidation(commentId, userId);
        comment.setText(commentDto.getText());
        log.info("Комментарий с id {} обновлен", commentId);
        return commentMapper.toDto(commentRepository.save(comment));
    }

    public Comment commentValidation(Long commentId) {
        return commentRepository.findById(commentId).orElseThrow(() ->
                new NotFoundException("Комментарий с id: {} не найден", commentId));
    }

    public Comment commentWithUserValidation(Long commentId, Long userId) {
        Comment comment = commentValidation(commentId);
        if (!comment.getUser().getId().equals(userId)) {
            throw new WrongOwnerException("Пользователь с id: {} не является владельцем комментария с id: {}",
                    userId, commentId);
        }
        return comment;
    }
}