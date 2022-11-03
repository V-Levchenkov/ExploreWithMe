package ru.practicum.requests.dto;

import org.springframework.stereotype.Component;
import ru.practicum.event.model.Event;
import ru.practicum.requests.model.Status;
import ru.practicum.requests.model.Request;
import ru.practicum.users.model.User;
import ru.practicum.utilits.DateFormatterCustom;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class RequestMapper {

    private final DateFormatterCustom formatter = new DateFormatterCustom();

    public RequestDto toRequestDtoList(Request request) {
        return new RequestDto(request.getId(),
                request.getEvent().getId(),
                request.getRequester().getId(),
                formatter.dateToString(request.getCreated()),
                request.getStatus().toString());
    }

    public Request fromRequestDto(RequestDto requestDto) {
        return new Request(requestDto.getId(),
                new Event(),
                new User(),
                formatter.stringToDate(requestDto.getCreated()),
                Status.valueOf(requestDto.getStatus()));
    }

    public List<RequestDto> toRequestDtoList(List<Request> requests) {
        return requests
                .stream()
                .map(this::toRequestDtoList)
                .collect(Collectors.toList());
    }
}
