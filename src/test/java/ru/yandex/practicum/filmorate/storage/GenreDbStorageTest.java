package ru.yandex.practicum.filmorate.storage;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.mappers.GenreRowMapper;
import ru.yandex.practicum.filmorate.model.Genre;

@JdbcTest
@AutoConfigureTestDatabase
@Import({GenreDbStorage.class, GenreRowMapper.class})
public class GenreDbStorageTest {
    @Autowired
    private GenreDbStorage storage;

    @Test
    public void testAddAndGetGenres() {
        storage.addGenre(new Genre(0, "genreTest"));
        storage.addGenre(new Genre(0, "genreTest2"));
        storage.addGenre(new Genre(0, "genreTest3"));
        Integer expected = 4;
        Assertions.assertEquals(expected, storage.getGenres().size(), "Не корректное количество жанров");
    }

    @Test
    public void testGetGenre() {
        storage.addGenre(new Genre(0, "genreTest"));
        Genre test = storage.addGenre(new Genre(0, "genreTest2"));
        Assertions.assertEquals("genreTest2", storage.getGenre(test.getId()).getName(), "Не получили жанр по id");
    }
}
