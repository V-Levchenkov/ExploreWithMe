package ru.practicum.event.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import ru.practicum.categories.dto.CategoryMapper;
import ru.practicum.categories.service.CategoryService;
import ru.practicum.event.client.EventClient;
import ru.practicum.event.dto.*;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.EventForSpecific;
import ru.practicum.event.model.State;
import ru.practicum.event.storage.EventRepository;
import ru.practicum.exceptions.NotFoundException;
import ru.practicum.exceptions.ValidationException;
import ru.practicum.requests.dto.RequestDto;
import ru.practicum.requests.dto.RequestMapper;
import ru.practicum.requests.model.Request;
import ru.practicum.requests.model.Status;
import ru.practicum.requests.service.RequestService;
import ru.practicum.users.dto.UserMapper;
import ru.practicum.users.service.UserService;
import ru.practicum.utilits.DateFormatterCustom;
import ru.practicum.utilits.PageableRequest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Slf4j
@Service
public class EventServiceImpl implements EventService {

    private final CategoryService categoryService;
    private final EventMapper eventMapper;
    private final CategoryMapper categoryMapper;
    private final EventRepository repository;
    private final DateFormatterCustom formatter;
    private final UserService userService;
    private final RequestService requestService;
    private final RequestMapper requestMapper;
    private final UserMapper userMapper;
    private final EventClient eventClient;

    @Override
    public List<FullEventDto> getAllByAdmin(EventForSpecific event) {
        Specification<Event> specification = EventSpecifications.getAdminSpecifications(event);
        List<Event> events = repository.findAll(specification,
                        getPageable(event.getFrom(), event.getSize(), Sort.unsorted()))
                .getContent();
        log.info("Администратором получен список событий {}", events);
        return eventMapper.toDto(events);
    }

    @Override
    public FullEventDto updateByAdmin(Long eventId, NewEventDto eventDto) {
        Event event = eventValidation(eventId);
        eventDtoValidation(eventDto, event);
        Optional.ofNullable(eventDto.getLocation()).ifPresent(event::setLocation);
        Optional.ofNullable(eventDto.getRequestModeration()).ifPresent(event::setRequestModeration);
        repository.save(event);
        log.info("Обновлено событие c id {}", eventId);
        return eventMapper.toDto(event);
    }

    @Override
    public FullEventDto publishEventAdmin(Long eventId) {
        Event event = eventValidation(eventId);
        if (event.getEventDate().plusHours(1).isAfter(LocalDateTime.now()) &&
                event.getState() == State.PENDING) {
            event.setPublishedOn(LocalDateTime.now());
            event.setState(State.PUBLISHED);
            repository.save(event);
            log.info("Опубликовано событие c id: {}", eventId);
        }
        return eventMapper.toDto(event);
    }

    @Override
    public FullEventDto rejectEventAdmin(Long eventId) {
        Event event = eventValidation(eventId);
        if (event.getState() == State.PENDING) {
            event.setState(State.CANCELED);
            repository.save(event);
            log.info("Администратором отклонено событие c id: {}", eventId);
        }
        return eventMapper.toDto(event);
    }

    @Override
    public List<ShortEventDto> getAllPrivate(Long userId, Integer from, Integer size) {
        List<Event> events = repository.findAllByInitiatorId(userId, getPageable(from, size, Sort.unsorted()));
        log.info("Получен список событий {}", events);
        return eventMapper.toShortDto(events);
    }

    @Override
    public FullEventDto updatePrivate(Long userId, NewEventDto eventDto) {
        Event event = eventValidation(eventDto.getEventId());
        eventDtoValidation(eventDto, event);
        if (!userId.equals(event.getInitiator().getId())) {
            throw new ValidationException("Редактировать событие может только его инициатор");
        }
        if (event.getState() == State.PUBLISHED) {
            throw new ValidationException("Редактировать можно только отмененные события " +
                    "или события в состоянии ожидания модерации");
        }
        if (event.getState() == State.CANCELED) {
            event.setState(State.PENDING);
        }
        repository.save(event);
        log.info("Обновлено событие {}", event);
        return eventMapper.toDto(event);
    }

    @Override
    public FullEventDto addPrivate(Long userId, NewEventDto eventDto) {
        eventDateValidation(eventDto.getEventDate());
        Event event = eventMapper.fromDto(eventDto);
        event.setCategory(categoryMapper.fromCategoryDto(categoryService.getCategoryById(eventDto.getCategory())));
        event.setInitiator(userMapper.fromDtoToUser(userService.getAllUsersList(new Long[]{userId}, 0, 1).get(0)));
        repository.save(event);
        log.info("Добавлено событие {}", event);
        return eventMapper.toDto(event);
    }

    @Override
    public FullEventDto getByIdPrivate(Long userId, Long eventId) {
        Event event = repository.findByInitiatorIdAndId(userId, eventId);
        if (event == null) {
            throw new NotFoundException("Событие id: {} не найдено", eventId);
        }
        log.info("Получено событие: {} ", event);
        return eventMapper.toDto(event);
    }

    @Override
    public FullEventDto cancelPrivate(Long userId, Long eventId) {
        Event event = eventValidation(eventId);
        if (!userId.equals(event.getInitiator().getId())) {
            throw new ValidationException("Отменить событие может только его инициатор");
        }
        if (event.getState().equals(State.CANCELED) || event.getState().equals(State.PUBLISHED)) {
            throw new ValidationException("Отменить можно только событие в состоянии ожидания модерации");
        }
        event.setState(State.CANCELED);
        repository.save(event);
        log.info("Отменено событие c id: {}", eventId);
        return eventMapper.toDto(event);
    }

    @Override
    public List<RequestDto> getEventRequestsPrivate(Long userId, Long eventId) {
        Event event = eventValidation(eventId);
        if (!userId.equals(event.getInitiator().getId())) {
            throw new ValidationException("Просматривать заявки на событие может только его инициатор");
        }
        List<RequestDto> requests = requestService.getAllByEventRequests(eventId);
        log.info("Получен список заявок {}", requests);
        return requests;
    }

    @Override
    public RequestDto confirmRequestPrivate(Long userId, Long eventId, Long requestId) {
        Event event = eventValidation(eventId);
        Request request = requestService.getByRequestId(requestId);
        if (event.getParticipantLimit() == 0 || !event.getRequestModeration()) {
            return requestMapper.toRequestDtoList(request);
        }
        if (event.getConfirmedRequests() == event.getParticipantLimit()) {
            throw new ValidationException("Достигнут лимит запросов на участие в событии eventId: ", eventId);
        }
        increaseConfirmedRequestsPrivate(event);
        request.setStatus(Status.CONFIRMED);
        if (event.getConfirmedRequests() == event.getParticipantLimit()) {
            requestService.rejectAllRequest(eventId);
        }
        requestService.saveRequest(request);
        log.info("Подтверждена заявка с id: {}", requestId);
        return requestMapper.toRequestDtoList(request);
    }

    @Override
    public RequestDto rejectRequestPrivate(Long userId, Long eventId, Long requestId) {
        eventValidation(eventId);
        Request request = requestService.getByRequestId(requestId);
        if (request.getStatus() == Status.REJECTED || request.getStatus() == Status.CANCELED) {
            throw new ValidationException("Заявка уже отменена или отклонена");
        }
        request.setStatus(Status.REJECTED);
        requestService.saveRequest(request);
        log.info("Отклонена заявка c id: {}", requestId);
        return requestMapper.toRequestDtoList(request);
    }

    @Override
    public void increaseConfirmedRequestsPrivate(Event event) {
        event.setConfirmedRequests(event.getConfirmedRequests() + 1);
        repository.save(event);
    }

    @Override
    public void decreaseConfirmedRequestsPrivate(Event event) {
        if (event.getConfirmedRequests() > 0) {
            event.setConfirmedRequests(event.getConfirmedRequests() - 1);
            repository.save(event);
        }
    }

    @Override
    public Event getEventByIdPrivate(Long eventId) {
        return eventValidation(eventId);
    }

    @Override
    public List<ShortEventDto> getAllPublic(EventForSpecific event) {
        sortValidation(event.getSort());
        Specification<Event> specification = EventSpecifications.getUserSpecifications(event);
        List<Event> events = repository.findAll(specification,
                        getPageable(event.getFrom(), event.getSize(), getSorting(event.getSort())))
                .getContent();
        setViews(events);
        log.info("Получен список событий {}", events);
        return eventMapper.toShortDto(events);
    }

    @Override
    public FullEventDto getByIdPublic(Long eventId) {
        Event event = repository.findByIdAndStateLike(eventId, State.PUBLISHED);
        if (event == null) {
            throw new NotFoundException("Событие с id: {} не найдено", eventId);
        }
        setViews(List.of(event));
        log.info("Получено событие с id: {}", eventId);
        return eventMapper.toDto(event);
    }

    private void setViews(List<Event> events) {
        events.forEach(event -> {
            List<ViewStatsDto> views = eventClient.getHits(event.getCreatedOn(), LocalDateTime.now(),
                            new String[]{"/events/" + event.getId()}, false)
                    .getBody();
            if (views != null && views.size() > 0) {
                event.setViews(views.get(0).getHits());
            }
        });
    }

    private Event eventValidation(Long eventId) {
        return repository.findById(eventId).orElseThrow(() ->
                new NotFoundException("Событие с id: {} не найдено", eventId));
    }

    private void eventDateValidation(String eventDate) {
        String[] lines = eventDate.split(" ");
        LocalDateTime dateTime = LocalDateTime.of(LocalDate.parse(lines[0]), LocalTime.parse(lines[1]));
        if (dateTime.isBefore(LocalDateTime.now().plusHours(2))) {
            throw new ValidationException("Дата и время начала события не может быть раньше, " +
                    "чем через два часа от текущего момента");
        }
    }

    private void sortValidation(String sort) {
        if (!EventSort.EVENT_DATE.toString().equals(sort) && !EventSort.VIEWS.toString().equals(sort)) {
            throw new ValidationException("Формат сортировки не верный");
        }
    }

    private Pageable getPageable(Integer from, Integer size, Sort sort) {
        return new PageableRequest(from, size, sort);
    }

    private Sort getSorting(String sort) {
        sort = EventSort.EVENT_DATE.toString().equals(sort) ? "eventDate" : "views";
        return Sort.by(Sort.Direction.DESC, sort);
    }

    private void eventDtoValidation(NewEventDto eventDto, Event event) {
        if (eventDto.getAnnotation() != null) {
            event.setAnnotation(eventDto.getAnnotation());
        }
        if (eventDto.getCategory() != null) {
            event.setCategory(categoryMapper.fromCategoryDto(categoryService.getCategoryById(eventDto.getCategory())));
        }
        if (eventDto.getDescription() != null) {
            event.setDescription(eventDto.getDescription());
        }
        if (eventDto.getEventDate() != null) {
            eventDateValidation(eventDto.getEventDate());
            event.setEventDate(formatter.stringToDate(eventDto.getEventDate()));
        }
        if (eventDto.getPaid() != null) {
            event.setPaid(eventDto.getPaid());
        }
        if (eventDto.getParticipantLimit() != 0) {
            event.setParticipantLimit(eventDto.getParticipantLimit());
        }
        if (eventDto.getTitle() != null) {
            event.setTitle(eventDto.getTitle());
        }
    }
}
