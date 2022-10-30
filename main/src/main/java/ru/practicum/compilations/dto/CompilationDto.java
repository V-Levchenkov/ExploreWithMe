package ru.practicum.compilations.dto;

import lombok.*;
import ru.practicum.event.dto.ShortEventDto;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompilationDto {

    private Long id;
    private List<ShortEventDto> events;
    private boolean pinned;
    private String title;

}
