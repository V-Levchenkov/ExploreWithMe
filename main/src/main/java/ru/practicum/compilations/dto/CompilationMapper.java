package ru.practicum.compilations.dto;

import org.springframework.stereotype.Component;
import ru.practicum.compilations.model.Compilation;
import ru.practicum.event.model.Event;
import ru.practicum.event.dto.EventMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class CompilationMapper {

    private final EventMapper mapper = new EventMapper();

    public CompilationDto toDto(Compilation compilation) {
        return new CompilationDto(compilation.getId(),
                mapper.toShortDto(compilation.getEvents()),
                compilation.isPinned(),
                compilation.getTitle());
    }

    public Compilation fromDto(NewCompilationDto compilationDto) {
        return new Compilation(compilationDto.getId(),
                getIds(compilationDto.getEvents()),
                compilationDto.isPinned(),
                compilationDto.getTitle());
    }

    public List<CompilationDto> toDto(List<Compilation> compilations) {
        return compilations
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    private List<Event> getIds(List<Long> ids) {
        List<Event> events = new ArrayList<>();
        for (int i = 0; i < ids.size(); i++) {
            events.add(new Event());
            events.get(i).setId(ids.get(i));
        }
        return events;
    }
}
