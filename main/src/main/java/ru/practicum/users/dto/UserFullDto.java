package ru.practicum.users.dto;

import lombok.*;

import javax.validation.constraints.Email;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserFullDto {

    private Long id;
    private String name;

    @Email
    private String email;
}
