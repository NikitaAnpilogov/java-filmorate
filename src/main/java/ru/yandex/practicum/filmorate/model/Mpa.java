package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(of = {"id"})
@NoArgsConstructor
public class Mpa {
    private Integer id;
    private String name;

    public Mpa(Integer id, String name) {
        this.id = id;
        this.name = name;
    }
}
