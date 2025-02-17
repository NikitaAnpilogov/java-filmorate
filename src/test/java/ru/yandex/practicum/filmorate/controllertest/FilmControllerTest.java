package ru.yandex.practicum.filmorate.controllertest;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import java.time.LocalDate;
import java.util.Collection;

public class FilmControllerTest {
    Film normalFilm = new Film(1, "name1", "description1", LocalDate.of(2000, 1, 1), 30);
    Film emptyName = new Film(1, "", "description2", LocalDate.of(2001, 1, 1), 30);
    Film longDescription = new Film(1, "name3", "description3description3description3description3description3description3description3description3description3description3description3description3description3description3description3description3description3description3description3description3",
            LocalDate.of(2002, 1, 1), 30);
    Film earlyDate1 = new Film(1, "name5", "description5", LocalDate.of(1895, 12, 28), 30);
    Film earlyDate2 = new Film(1, "name6", "description6", LocalDate.of(1895, 12, 27), 30);
    Film earlyDate3 = new Film(1, "name7", "description7", LocalDate.of(1895, 12, 29), 30);
    Film zeroDuration = new Film(1, "name8", "description8", LocalDate.of(2004, 1, 1), 0);
    Film negativeDuration = new Film(1, "name9", "description9", LocalDate.of(2005, 1, 1), -10);
    Film filmWithoutId = new Film("name10", "description10", LocalDate.of(2000, 1, 1), 30);
    Film notFoundId = new Film(56, "name11", "description11", LocalDate.of(2000, 1, 1), 30);
    FilmController filmController;

    @BeforeEach
    void start() {
        filmController = new FilmController();
    }

    @Test
    void shouldAddFilm() {
        filmController.addFilm(normalFilm);
        Collection<Film> collection = filmController.getFilms();
        Assertions.assertEquals(1, collection.size(), "Фильм не добавился");
    }

    @Test
    void testEmptyName() {
        Assertions.assertThrows(ValidationException.class, () -> filmController.addFilm(emptyName), "Валидация для пустого имени не прошла");
    }

    @Test
    void testLongDescription() {
        Assertions.assertThrows(ValidationException.class, () -> filmController.addFilm(longDescription), "Валидация для длинного описания не прошла");
    }

    @Test
    void testEarlyDate1() {
        filmController.addFilm(earlyDate1);
        Collection<Film> collection = filmController.getFilms();
        Assertions.assertEquals(1, collection.size(), "Фильм не добавился");
    }

    @Test
    void testEarlyDate2() {
        Assertions.assertThrows(ValidationException.class, () -> filmController.addFilm(earlyDate2), "Валидация для даты не прошла");
    }

    @Test
    void testEarlyDate3() {
        filmController.addFilm(earlyDate3);
        Collection<Film> collection = filmController.getFilms();
        Assertions.assertEquals(1, collection.size(), "Фильм не добавился");
    }

    @Test
    void testZeroDuration() {
        Assertions.assertThrows(ValidationException.class, () -> filmController.addFilm(zeroDuration), "Валидация для нулевой продолжительности не прошла");
    }

    @Test
    void testNegativeDuration() {
        Assertions.assertThrows(ValidationException.class, () -> filmController.addFilm(negativeDuration), "Валидация для отрицательной продолжительности не прошла");
    }

    @Test
    void testUpdateEmptyName() {
        filmController.addFilm(normalFilm);
        Assertions.assertThrows(ValidationException.class, () -> filmController.updateFilm(emptyName), "Валидация для пустого имени не прошла");
    }

    @Test
    void testUpdateLongDescription() {
        filmController.addFilm(normalFilm);
        Assertions.assertThrows(ValidationException.class, () -> filmController.updateFilm(longDescription), "Валидация для длинного описания не прошла");
    }

    @Test
    void testUpdateEarlyDate() {
        filmController.addFilm(normalFilm);
        Assertions.assertThrows(ValidationException.class, () -> filmController.updateFilm(earlyDate2), "Валидация для даты не прошла");
    }

    @Test
    void testUpdateZeroDuration() {
        filmController.addFilm(normalFilm);
        Assertions.assertThrows(ValidationException.class, () -> filmController.updateFilm(zeroDuration), "Валидация для нулевой продолжительности не прошла");
    }

    @Test
    void testUpdateNegativeDuration() {
        filmController.addFilm(normalFilm);
        Assertions.assertThrows(ValidationException.class, () -> filmController.updateFilm(negativeDuration), "Валидация для отрицательной продолжительности не прошла");
    }

    @Test
    void shouldGetFilms() {
        filmController.addFilm(normalFilm);
        filmController.addFilm(earlyDate1);
        filmController.addFilm(earlyDate3);
        Collection<Film> collection = filmController.getFilms();
        Assertions.assertEquals(3, collection.size(), "Не получили список фильмов");
    }

    @Test
    void testUpdateWithoutId() {
        filmController.addFilm(normalFilm);
        Assertions.assertThrows(NotFoundException.class, () -> filmController.updateFilm(filmWithoutId), "Валидация для фильма без ID не прошла");
    }

    @Test
    void testNotFoundId() {
        filmController.addFilm(normalFilm);
        Assertions.assertThrows(NotFoundException.class, () -> filmController.updateFilm(notFoundId), "Валидация поиска ID не прошла");
    }
}
