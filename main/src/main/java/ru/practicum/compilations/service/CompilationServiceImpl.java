package ru.practicum.compilations.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.compilations.dto.CompilationDto;
import ru.practicum.compilations.dto.CompilationMapper;
import ru.practicum.compilations.dto.NewCompilationDto;
import ru.practicum.compilations.model.Compilation;
import ru.practicum.compilations.storage.CompilationRepository;
import ru.practicum.event.service.EventService;
import ru.practicum.exceptions.NotFoundException;
import ru.practicum.utilits.PageableRequest;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Service
@Slf4j
public class CompilationServiceImpl implements CompilationService {

    private final CompilationMapper compilationMapper;
    private final EventService eventService;
    private final CompilationRepository compilationRepository;

    @Override
    public List<CompilationDto> getAllCompilationList(boolean pinned, Integer from, Integer size) {
        List<Compilation> compilations = compilationRepository.findAllByPinnedIs(pinned,
                getPageable(from, size, Sort.unsorted()));
        log.info("Получен список подборок {}", compilations);
        return compilationMapper.toDto(compilations);
    }

    @Override
    public CompilationDto getCompilationById(Long compilationId) {
        Compilation compilation = checkCompilation(compilationId);
        log.info("Получена подборка {}", compilation);
        return compilationMapper.toDto(compilation);
    }

    @Override
    public CompilationDto addCompilation(NewCompilationDto compilationDto) {
        Compilation compilation = compilationRepository.save(compilationMapper.fromDto(compilationDto));
        compilation.getEvents().replaceAll(event -> eventService.getEventByIdPrivate(event.getId()));
        log.info("Добавлена подборка событий {}", compilation);
        return compilationMapper.toDto(compilation);
    }

    @Override
    public void deleteCompById(Long compilationId) {
        compilationRepository.deleteById(compilationId);
        log.info("Удалена подборка c id {}", compilationId);
    }

    @Override
    @Transactional
    public void deleteCompilationEvent(Long compilationId, Long eventId) {
        Compilation compilation = checkCompilation(compilationId);
        compilationRepository.deleteEvent(compilationId, eventId);
        log.info("Из подборки {} удалено событие с id {}", compilation, eventId);
    }

    @Override
    @Transactional
    public void addCompilationEvent(Long compilationId, Long eventId) {
        Compilation compilation = checkCompilation(compilationId);
        compilationRepository.addEvent(compilationId, eventId);
        log.info("В подборку {} добавлено событие с id {}", compilation, eventId);
    }

    @Override
    @Transactional
    public void pinCompilation(boolean pin, Long compilationId) {
        compilationRepository.pinningCompilation(pin, compilationId);
        Compilation compilation = checkCompilation(compilationId);
        if (pin) {
            log.info("Подборка закреплена {}", compilation);
        } else {
            log.info("Подборка откреплена {}", compilation);
        }
    }

    private Compilation checkCompilation(Long compilationId) {
        Optional<Compilation> category = compilationRepository.findById(compilationId);
        if (category.isEmpty()) {
            throw new NotFoundException("Подборка с id: {} не найдена", compilationId);
        }
        return category.get();
    }

    private Pageable getPageable(Integer from, Integer size, Sort sort) {
        return new PageableRequest(from, size, sort);
    }
}
