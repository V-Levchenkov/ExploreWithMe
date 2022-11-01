package ru.practicum.requests.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.State;
import ru.practicum.event.service.EventService;
import ru.practicum.exceptions.NotFoundException;
import ru.practicum.exceptions.ValidationException;
import ru.practicum.requests.dto.RequestMapper;
import ru.practicum.requests.model.Status;
import ru.practicum.requests.dto.RequestDto;
import ru.practicum.requests.model.Request;
import ru.practicum.requests.storage.RequestRepository;
import ru.practicum.users.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@AllArgsConstructor(onConstructor_ = {@Lazy})
@Slf4j
@Service
public class RequestServiceImpl implements RequestService {

    private final EventService eventService;
    private final UserService userService;
    private final RequestMapper requestMapper;
    private final RequestRepository requestRepository;

    @Override
    public List<RequestDto> getAllByUserRequests(Long userId) {
        List<Request> requests = requestRepository.findAllByRequesterId(userId);
        log.info("Получен список заявок пользователя с id: {}", userId);
        return requestMapper.toRequestDtoList(requests);
    }

    @Override
    public RequestDto addRequest(Long userId, Long eventId) {
        Event event = eventService.getEventByIdPrivate(eventId);
        if (event.getInitiator().getId().equals(userId)) {
            throw new ValidationException("Нельзя добавить запрос на участие в своем событии");
        }
        if (!event.getState().equals(State.PUBLISHED)) {
            throw new ValidationException("Нельзя участвовать в неопубликованном событии");
        }
        if (event.getConfirmedRequests() == event.getParticipantLimit()) {
            throw new ValidationException("Достигнут лимит запросов на участие");
        }
        Request request = new Request();
        if (event.getRequestModeration()) {
            request.setStatus(Status.PENDING);
        } else {
            request.setStatus(Status.CONFIRMED);
            eventService.increaseConfirmedRequestsPrivate(event);
        }
        request.setEvent(event);
        request.setRequester(userService.getUserById(userId));
        request.setCreated(LocalDateTime.now());
        log.info("Заявка на участие в событии с id: {} для пользователя: {} добавлена", eventId, userId);
        return requestMapper.toRequestDtoList(requestRepository.save(request));
    }

    @Override
    public RequestDto cancelRequest(Long userId, Long requestId) {
        Request request = requestValidator(requestId);
        if (!request.getRequester().getId().equals(userId)) {
            throw new ValidationException("Отменить можно только свою заявку");
        }
        if (request.getStatus().equals(Status.REJECTED) || request.getStatus().equals(Status.CANCELED)) {
            throw new ValidationException("Заявка уже отменена/отклонена");
        }
        if (request.getStatus().equals(Status.CONFIRMED)) {
            eventService.decreaseConfirmedRequestsPrivate(eventService.getEventByIdPrivate(request.getEvent().getId()));
        }
        request.setStatus(Status.CANCELED);
        log.info("Заявка на событие c id: {} отменена", requestId);
        return requestMapper.toRequestDtoList(requestRepository.save(request));
    }

    @Override
    public List<RequestDto> getAllByEventRequests(Long eventId) {
        log.info("Получен список заявок события с id: {}", eventId);
        return requestMapper.toRequestDtoList(requestRepository.findAllByEventId(eventId));
    }

    @Override
    public Request getByRequestId(Long requestId) {
        log.info("Получен запрос с id: {}", requestId);
        return requestValidator(requestId);
    }

    @Override
    public void saveRequest(Request request) {
        log.info("Запрос: {} сохранен", request);
        requestRepository.save(request);
    }

    @Override
    public void rejectAllRequest(Long eventId) {
        log.info("Получен запрос на смену статуса на reject для eventId: {}", eventId);
        requestRepository.rejectAll(eventId);
    }

    private Request requestValidator(Long requestId) {
        return requestRepository.findById(requestId).orElseThrow(() ->
                new NotFoundException("Заявка с id: {} не найдена", requestId));
    }
}
