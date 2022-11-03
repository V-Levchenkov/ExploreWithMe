package ru.practicum.compilations.dto;

import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NewCompilationDto {

    private Long id;
    private List<Long> events;
    private boolean pinned;
    private String title;

}
