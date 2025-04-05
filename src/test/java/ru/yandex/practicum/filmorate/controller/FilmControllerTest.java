package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

public class FilmControllerTest {
    Film normalFilm = new Film(1, "name1", "description1", LocalDate.of(2000, 1, 1), 30, new Mpa(1, "G"));
    Film normalFilm2 = new Film(1, "name2", "description2", LocalDate.of(2000, 1, 1), 30, new Mpa(1, "G"));
    Film normalFilm3 = new Film(1, "name3", "description3", LocalDate.of(2000, 1, 1), 30, new Mpa(1, "G"));
    Film emptyName = new Film(1, "", "description2", LocalDate.of(2001, 1, 1), 30, new Mpa(1, "G"));
    Film longDescription = new Film(1, "name3", "description3description3description3description3description3description3description3description3description3description3description3description3description3description3description3description3description3description3description3description3",
            LocalDate.of(2002, 1, 1), 30, new Mpa(1, "G"));
    Film earlyDate1 = new Film(1, "name5", "description5", LocalDate.of(1895, 12, 28), 30, new Mpa(1, "G"));
    Film earlyDate2 = new Film(1, "name6", "description6", LocalDate.of(1895, 12, 27), 30, new Mpa(1, "G"));
    Film earlyDate3 = new Film(1, "name7", "description7", LocalDate.of(1895, 12, 29), 30, new Mpa(1, "G"));
    Film zeroDuration = new Film(1, "name8", "description8", LocalDate.of(2004, 1, 1), 0, new Mpa(1, "G"));
    Film negativeDuration = new Film(1, "name9", "description9", LocalDate.of(2005, 1, 1), -10, new Mpa(1, "G"));
    Film filmWithoutId = new Film("name10", "description10", LocalDate.of(2000, 1, 1), 30, new Mpa(1, "G"));
    Film notFoundId = new Film(56, "name11", "description11", LocalDate.of(2000, 1, 1), 30, new Mpa(1, "G"));
    FilmController filmController;

    @BeforeEach
    void start() { // Инициализирую перед каждым тестом, чтобы обнулить контроллер и тесты не зависели друг от друга
        filmController = new FilmController(new FilmService(new InMemoryFilmStorage(), new InMemoryUserStorage()));
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

    @Test
    void shouldAddLike() {
        InMemoryUserStorage inMemoryUserStorage = new InMemoryUserStorage();
        User normalUser = new User(1, "abc@mail.ru", "login1", "name1", LocalDate.of(2000, 1, 1));
        inMemoryUserStorage.addUser(normalUser);
        filmController = new FilmController(new FilmService(new InMemoryFilmStorage(), inMemoryUserStorage));
        filmController.addFilm(normalFilm);
        filmController.addLike(normalFilm.getId(), 1);
        Assertions.assertEquals(1, normalFilm.getLikes().size(), "Не лайкнули фильм");
    }

    @Test
    void shouldRemoveLike() {
        InMemoryUserStorage inMemoryUserStorage = new InMemoryUserStorage();
        User normalUser = new User(1, "abc@mail.ru", "login1", "name1", LocalDate.of(2000, 1, 1));
        inMemoryUserStorage.addUser(normalUser);
        filmController = new FilmController(new FilmService(new InMemoryFilmStorage(), inMemoryUserStorage));
        filmController.addFilm(normalFilm);
        filmController.addLike(normalFilm.getId(), 1);
        filmController.removeLike(normalFilm.getId(), 1);
        Assertions.assertEquals(0, normalFilm.getLikes().size(), "Не удалили лайк");
    }

    @Test
    void shouldGetPopularFilms() {
        InMemoryUserStorage inMemoryUserStorage = new InMemoryUserStorage();
        User normalUser = new User(1, "abc@mail.ru", "login1", "name1", LocalDate.of(2000, 1, 1));
        User normalUser2 = new User(1, "abc2@mail.ru", "login2", "name2", LocalDate.of(2000, 1, 1));
        User normalUser3 = new User(1, "abc3@mail.ru", "login3", "name3", LocalDate.of(2000, 1, 1));
        User normalUser4 = new User(1, "abc4@mail.ru", "login4", "name4", LocalDate.of(2000, 1, 1));
        User normalUser5 = new User(1, "abc5@mail.ru", "login5", "name5", LocalDate.of(2000, 1, 1));
        User normalUser6 = new User(1, "abc6@mail.ru", "login6", "name6", LocalDate.of(2000, 1, 1));
        User normalUser7 = new User(1, "abc7@mail.ru", "login7", "name7", LocalDate.of(2000, 1, 1));
        inMemoryUserStorage.addUser(normalUser);
        inMemoryUserStorage.addUser(normalUser2);
        inMemoryUserStorage.addUser(normalUser3);
        inMemoryUserStorage.addUser(normalUser4);
        inMemoryUserStorage.addUser(normalUser5);
        inMemoryUserStorage.addUser(normalUser6);
        inMemoryUserStorage.addUser(normalUser7);
        filmController = new FilmController(new FilmService(new InMemoryFilmStorage(), inMemoryUserStorage));
        filmController.addFilm(normalFilm);
        filmController.addFilm(normalFilm2);
        filmController.addFilm(normalFilm3);
        filmController.addLike(normalFilm.getId(), 1);
        filmController.addLike(normalFilm2.getId(), 2);
        filmController.addLike(normalFilm2.getId(), 3);
        filmController.addLike(normalFilm2.getId(), 4);
        filmController.addLike(normalFilm3.getId(), 5);
        filmController.addLike(normalFilm3.getId(), 6);
        List<Film> popular = filmController.getPopularFilms(3);
        System.out.println(popular);
        Assertions.assertEquals(3, popular.size(), "Не получили список популярных фильмов");
    }
}
