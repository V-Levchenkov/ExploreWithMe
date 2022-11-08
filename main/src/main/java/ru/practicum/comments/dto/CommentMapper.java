package ru.practicum.comments.dto;

import ru.practicum.comments.model.Comment;

public class CommentMapper {
    public static CommentDto toDto(Comment comment) {
        CommentDto dto = new CommentDto();
        dto.setId(comment.getId());
        dto.setText(comment.getText());
        dto.setEventId(comment.getEvent().getId());
        dto.setUserId(comment.getUser().getId());
        return dto;
    }
}