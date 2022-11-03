package ru.practicum.compilations.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.compilations.dto.CompilationDto;
import ru.practicum.compilations.dto.NewCompilationDto;
import ru.practicum.compilations.service.CompilationService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class CompilationController {

    private final CompilationService compilationService;

    @PostMapping("/admin/compilations")
    public CompilationDto add(@RequestBody NewCompilationDto compilationDto) {
        return compilationService.addCompilation(compilationDto);
    }

    @DeleteMapping("/admin/compilations/{compId}")
    public void delete(@PathVariable Long compId) {
        compilationService.deleteCompById(compId);
    }

    @DeleteMapping("/admin/compilations/{compId}/events/{eventId}")
    public void deleteEvent(@PathVariable Long compId, @PathVariable Long eventId) {
        compilationService.deleteCompilationEvent(compId, eventId);
    }

    @PatchMapping("/admin/compilations/{compId}/events/{eventId}")
    public void addEvent(@PathVariable Long compId, @PathVariable Long eventId) {
        compilationService.addCompilationEvent(compId, eventId);
    }

    @PatchMapping("/admin/compilations/{compId}/pin")
    public void pinCompilation(@PathVariable Long compId) {
        compilationService.pinCompilation(true, compId);
    }

    @DeleteMapping("/admin/compilations/{compId}/pin")
    public void unpinCompilation(@PathVariable Long compId) {
        compilationService.pinCompilation(false, compId);
    }


    @GetMapping("/compilations")
    public List<CompilationDto> getAll(@RequestParam(value = "pinned", required = false) boolean pinned,
                                       @PositiveOrZero @RequestParam(value = "from", defaultValue = "0")
                                       Integer from,
                                       @Positive @RequestParam(value = "size", defaultValue = "10")
                                       Integer size) {
        return compilationService.getAllCompilationList(pinned, from, size);
    }

    @GetMapping("/compilations/{compId}")
    public CompilationDto getById(@PathVariable Long compId) {
        return compilationService.getCompilationById(compId);
    }
}