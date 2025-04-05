package ru.yandex.practicum.filmorate.storage;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.mappers.FilmRowMapper;
import ru.yandex.practicum.filmorate.mappers.GenreRowMapper;
import ru.yandex.practicum.filmorate.mappers.MpaRowMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.time.LocalDate;
import java.util.Set;

@JdbcTest
@AutoConfigureTestDatabase
@Import({FilmDbStorage.class, FilmRowMapper.class, MpaRowMapper.class, GenreRowMapper.class})
public class FilmDbStorageTest {
    Mpa mpa = new Mpa(1, "Mpa1");
    private Film film = new Film("name1", "description1", LocalDate.of(1900, 1, 1), 120, mpa);
    private Film filmWithNullMpa = new Film("name2", "description2", LocalDate.of(1902, 1, 1), 100, new Mpa());
    private Film filmWithGenres = new Film("name3", "description3", LocalDate.of(1903, 1, 1), 90, new Mpa());

    @Autowired
    private FilmDbStorage storage;

    @Test
    public void testAddAndGetFilms() {
        storage.addFilm(film);
        storage.addFilm(filmWithNullMpa);
        filmWithGenres.setGenres(Set.of(new Genre(1, "Genre1")));
        storage.addFilm(filmWithGenres);
        Integer expected = 3;
        Assertions.assertEquals(expected, storage.getFilms().size(), "Не корректный размер списка фильмов");
    }

    @Test
    public void testGetFilm() {
        storage.addFilm(film);
        Film test = storage.addFilm(filmWithNullMpa);
        Assertions.assertEquals(filmWithNullMpa.getName(), storage.getFilm(test.getId()).getName(), "Не получили фильм по id");
    }

    @Test
    public void testUpdateFilm() {
        Film test = storage.addFilm(film);
        test.setDuration(150);
        test = storage.updateFilm(test);
        Assertions.assertEquals(150, test.getDuration(), "Фильм не обновился");
    }
}
