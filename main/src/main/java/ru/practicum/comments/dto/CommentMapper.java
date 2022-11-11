package ru.practicum.comments.dto;

import org.springframework.stereotype.Component;
import ru.practicum.comments.model.Comment;
import ru.practicum.event.model.Event;
import ru.practicum.users.model.User;

@Component
public class CommentMapper {
    public CommentDto toDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .userId(comment.getUser().getId())
                .eventId(comment.getEvent().getId())
                .text(comment.getText())
                .build();
    }

    public Comment toComment(CommentShortDto comment, User user, Event event) {
        return Comment.builder()
                .text(comment.getText())
                .user(user)
                .event(event)
                .build();
    }

}