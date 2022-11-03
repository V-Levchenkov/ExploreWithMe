package ru.practicum.event.service;

import ru.practicum.event.dto.FullEventDto;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.dto.ShortEventDto;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.EventForSpecific;
import ru.practicum.requests.dto.RequestDto;

import java.util.List;

public interface EventService {

    List<FullEventDto> getAllByAdmin(EventForSpecific criteria);

    FullEventDto updateByAdmin(Long eventId, NewEventDto eventDto);

    FullEventDto publishEventAdmin(Long eventId);

    FullEventDto rejectEventAdmin(Long eventId);

    List<ShortEventDto> getAllPrivate(Long userId, Integer from, Integer size);

    FullEventDto updatePrivate(Long userId, NewEventDto eventDto);

    FullEventDto addPrivate(Long userId, NewEventDto eventDto);

    FullEventDto getByIdPrivate(Long userId, Long eventId);

    FullEventDto cancelPrivate(Long userId, Long eventId);

    List<RequestDto> getEventRequestsPrivate(Long userId, Long eventId);

    RequestDto confirmRequestPrivate(Long userId, Long eventId, Long requestId);

    RequestDto rejectRequestPrivate(Long userId, Long eventId, Long requestId);

    void increaseConfirmedRequestsPrivate(Event event);

    void decreaseConfirmedRequestsPrivate(Event event);

    Event getEventByIdPrivate(Long eventId);

    List<ShortEventDto> getAllPublic(EventForSpecific criteria);

    FullEventDto getByIdPublic(Long eventId);
}
