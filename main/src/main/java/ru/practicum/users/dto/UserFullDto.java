package ru.practicum.users.dto;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserFullDto {

    private Long id;
    private String name;

    @Email
    @NotNull
    @NotBlank
    private String email;
}
