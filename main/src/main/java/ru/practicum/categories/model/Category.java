package ru.practicum.categories.model;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "categories")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Long id;
    @NotNull
    @Column(name = "name", nullable = false, length = 120, unique = true)
    private String name;

}
