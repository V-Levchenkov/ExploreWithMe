package ru.practicum.requests.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.requests.dto.RequestDto;
import ru.practicum.requests.service.RequestService;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping(path = "/users/{userId}/requests")
public class RequestController {

    private final RequestService requestService;

    @GetMapping
    public List<RequestDto> getAll(@PathVariable Long userId) {
        return requestService.getAllByUserRequests(userId);
    }

    @PostMapping
    public RequestDto add(@PathVariable Long userId, @RequestParam(value = "eventId") Long eventId) {
        return requestService.addRequest(userId, eventId);
    }

    @PatchMapping("/{requestId}/cancel")
    public RequestDto cancel(@PathVariable Long userId, @PathVariable Long requestId) {
        return requestService.cancelRequest(userId, requestId);
    }
}
