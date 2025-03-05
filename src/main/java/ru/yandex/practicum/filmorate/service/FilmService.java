package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;

@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final Set<Film> popularFilms = new TreeSet<>(Comparator.comparingInt(film -> film.getWhoLiked().size()));
    private final int POPULAR_DEFAULT = 10;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public Collection<Film> getFilms() {
        return filmStorage.getFilms();
    }

    public Film addFilm(Film film) {
        return filmStorage.addFilm(film);
    }

    public Film updateFilm(Film film) {
        return filmStorage.updateFilm(film);
    }

    public Film addLike(Integer filmId, Integer userId) {
        Film film = filmStorage.getFilm(filmId);
        userStorage.getUser(userId); // Проверка, есть ли такой User
        film.addLike(userId);
        popularFilms.add(film);
        filmStorage.updateFilm(film);
        return film;
    }

    public Film removeLike(Integer filmId, Integer userId) {
        Film film = filmStorage.getFilm(filmId);
        userStorage.getUser(userId); // Проверка, есть ли такой User
        film.removeLike(userId);
        popularFilms.add(film);
        filmStorage.updateFilm(film);
        return film;
    }

    public List<Film> getPopularFilms(Integer count) {
        if (count == null) {
            count = POPULAR_DEFAULT;
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
}
