package ru.practicum.users.model;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @NonNull
    @Column(name = "name", nullable = false, length = 70)
    private String name;
    @NotEmpty
    @NotNull
    @NonNull
    @Column(name = "email", nullable = false, length = 50)
    private String email;
}
