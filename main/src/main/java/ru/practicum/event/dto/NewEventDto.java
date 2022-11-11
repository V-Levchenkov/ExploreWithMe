package ru.practicum.event.dto;

import lombok.*;
import ru.practicum.event.model.Location;

import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NewEventDto {

    private Long eventId;

    @Size(max = 2000, min = 10)
    private String annotation;

    private Long category;

    @Size(max = 7000, min = 10)
    private String description;

    private String eventDate;
    private Location location;
    private Boolean paid;
    private int participantLimit;
    private Boolean requestModeration;

    @Size(max = 120, min = 3)
    private String title;
}
