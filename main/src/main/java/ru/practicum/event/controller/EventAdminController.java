package ru.practicum.event.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.model.EventForSpecific;
import ru.practicum.event.dto.FullEventDto;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.service.EventService;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping(path = "/admin/events")
public class EventAdminController {

    private final EventService eventService;

    @GetMapping
    public List<FullEventDto> getAll(EventForSpecific event) {
        return eventService.getAllByAdmin(event);
    }

    @PutMapping("/{eventId}")
    public FullEventDto update(@PathVariable Long eventId, @RequestBody NewEventDto eventDto) {
        return eventService.updateByAdmin(eventId, eventDto);
    }

    @PatchMapping("/{eventId}/publish")
    public FullEventDto publish(@PathVariable Long eventId) {
        return eventService.publishEventAdmin(eventId);
    }

    @PatchMapping("/{eventId}/reject")
    public FullEventDto reject(@PathVariable Long eventId) {
        return eventService.rejectEventAdmin(eventId);
    }
}
