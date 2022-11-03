package ru.practicum.requests.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequestDto {

    private Long id;
    private Long event;
    private Long requester;
    private String created;
    private String status;

}
