package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.exception.NotFoundException;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

@Data
@EqualsAndHashCode(of = {"id"})
@NoArgsConstructor
public class Film implements Comparable<Film> {
    private Integer id;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private Integer duration;
    private Set<Integer> likes = new HashSet<>();
    private Set<Genre> genres = new TreeSet<>();
    private Mpa mpa = new Mpa();

    public Film(Integer id, String name, String description, LocalDate releaseDate, Integer duration, Mpa mpa) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.mpa = mpa;
    }

    public Film(String name, String description, LocalDate releaseDate, Integer duration, Mpa mpa) {
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.mpa = mpa;
    }

    public void addLike(Integer userId) {
        likes.add(userId);
    }

    public void removeLike(Integer userId) {
        if (likes.contains(userId)) {
            likes.remove(userId);
        } else {
            throw new NotFoundException("like не был поставлен ранее");
        }
    }

    public boolean mpaIsNull() {
        return mpa == null;
    }

    @Override
    public int compareTo(Film film) {
        return this.getLikes().size() - film.getLikes().size();
    }
}
