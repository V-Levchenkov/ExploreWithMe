package ru.practicum.comments.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.comments.dto.CommentDto;
import ru.practicum.comments.dto.CommentDtoForUpdate;
import ru.practicum.comments.dto.CommentMapper;
import ru.practicum.comments.model.Comment;
import ru.practicum.comments.storage.CommentRepository;
import ru.practicum.event.model.Event;
import ru.practicum.event.service.EventService;
import ru.practicum.exceptions.NotFoundException;
import ru.practicum.exceptions.WrongEventAndUserException;
import ru.practicum.exceptions.WrongOwnerException;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final EventService eventService;

    @Override
    @Transactional
    public Comment addComment(Comment comment, Long userId, Long eventId) {
        eventAndUserValidation(userId, eventId);
        return commentRepository.save(comment);
    }

    @Override
    public CommentDto getComment(Long commentId) {
        Comment comment = commentValidation(commentId);
        log.info("Получен комментарий с id: {}", commentId);
        return CommentMapper.toDto(comment);
    }

    @Override
    @Transactional
    public void deleteComment(Long commentId, Long userId) {
        commentWithUserValidation(userId, commentId);
        commentRepository.deleteById(commentId);
        log.info("Удален комментарий c id {}", commentId);

    }

    @Override
    @Transactional
    public CommentDto updateComment(Long userId, Long commentId, CommentDtoForUpdate commentDto) {
        Comment comment = commentWithUserValidation(commentId, userId);
        return CommentMapper.toDto(commentRepository.save(comment));
    }

    private Comment commentValidation(Long commentId) {
        return commentRepository.findById(commentId).orElseThrow(() ->
                new NotFoundException("Подборка с id: {} не найдена", commentId));
    }

    private Comment commentWithUserValidation(Long commentId, Long userId) {
        Comment comment = commentValidation(commentId);
        if (comment.getUser().getId().equals(userId)) {
            throw new WrongOwnerException("Пользователь с id: {} не является владельцем комментария с id: {}",
                    userId, commentId);
        }
        return comment;
    }
    private void eventAndUserValidation(Long userId, Long eventId){
        Event event = eventService.getEventByIdPrivate(eventId);
        if (event.getInitiator().getId().equals(userId)) {
            throw new WrongEventAndUserException("Пользователь с id: {} не является владельцем события с id: {}",
                    userId, eventId);
        }
    }
}