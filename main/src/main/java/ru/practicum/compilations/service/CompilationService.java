package ru.practicum.compilations.service;

import ru.practicum.compilations.dto.CompilationDto;
import ru.practicum.compilations.dto.NewCompilationDto;

import java.util.List;

public interface CompilationService {

    List<CompilationDto> getAllCompilationList(boolean pinned, Integer from, Integer size);

    CompilationDto getCompilationById(Long compilationId);

    CompilationDto addCompilation(NewCompilationDto compilationDto);

    void deleteCompById(Long compilationId);

    void deleteCompilationEvent(Long compilationId, Long eventId);

    void addCompilationEvent(Long compilationId, Long eventId);

    void pinCompilation(boolean pin, Long compilationId);
}
