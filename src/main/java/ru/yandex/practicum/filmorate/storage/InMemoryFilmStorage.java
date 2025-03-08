package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.*;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Integer, Film> films = new HashMap<>();
    private final Set<Film> popularFilms = new TreeSet<>();
    private final int popularDefault = 10;

    private Integer getNextId() {
        int currentMaxId = films.keySet()
                .stream()
                .mapToInt(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    private void validate(Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            log.warn("Название фильма не может быть пустым");
            throw new ValidationException("Название фильма не может быть пустым");
        }
        if (film.getDescription().length() > 200) {
            log.warn("Максимальная длина описания — 200 символов");
            throw new ValidationException("Максимальная длина описания — 200 символов");
        }
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.warn("Дата релиза не может быть раньше 28 декабря 1895 года");
            throw new ValidationException("Дата релиза не может быть раньше 28 декабря 1895 года");
        }
        if (film.getDuration() <= 0) {
            log.warn("Продолжительность фильма должна быть положительной");
            throw new ValidationException("Продолжительность фильма должна быть положительной");
        }
    }

    public Collection<Film> getFilms() {
        return films.values();
    }

    public Film addFilm(Film film) {
        validate(film);
        film.setId(getNextId());
        films.put(film.getId(), film);
        popularFilms.add(film);
        log.info("Валидация прошла, фильм успешно добавлен");
        return film;
    }

    public Film updateFilm(Film film) {
        if (film.getId() == null) {
            log.warn("Id должен быть указан");
            throw new NotFoundException("Id должен быть указан");
        }
        if (films.containsKey(film.getId())) {
            validate(film);
            Film oldFilm = films.get(film.getId());
            oldFilm.setName(film.getName());
            oldFilm.setDescription(film.getDescription());
            oldFilm.setReleaseDate(film.getReleaseDate());
            oldFilm.setDuration(film.getDuration());
            oldFilm.setLike(film.getLike());
            popularFilms.add(oldFilm);
            log.info("Валидация прошла, фильм успешно обновлен");
            return oldFilm;
        } else {
            log.warn("Id не найден");
            throw new NotFoundException("Id не найден");
        }
    }

    public Film getFilm(Integer id) {
        if (films.containsKey(id)) {
            return films.get(id);
        } else {
            throw new NotFoundException("Фильм с таким id не найден");
        }
    }

    public List<Film> getPopularFilms(Integer count) {
        if (count == null) {
            count = popularDefault;
        }
        List<Film> popular = new ArrayList<>();
        for (Film film : popularFilms) {
            popular.add(film);
            --count;
            if (count <= 0) {
                break;
            }
        }
        return popular.reversed();
    }

    public void updatePopularFilm(Film film) {
        popularFilms.add(film);
    }
}
