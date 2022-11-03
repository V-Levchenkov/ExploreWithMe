package ru.practicum.requests.service;

import ru.practicum.requests.dto.RequestDto;
import ru.practicum.requests.model.Request;

import java.util.List;

public interface RequestService {

    RequestDto addRequest(Long userId, Long eventId);

    RequestDto cancelRequest(Long userId, Long requestId);

    List<RequestDto> getAllByUserRequests(Long userId);

    List<RequestDto> getAllByEventRequests(Long eventId);

    Request getByRequestId(Long requestId);

    void saveRequest(Request request);

    void rejectAllRequest(Long eventId);
}
