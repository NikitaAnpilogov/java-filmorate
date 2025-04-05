package ru.yandex.practicum.filmorate.storage;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.mappers.GenreRowMapper;

@JdbcTest
@AutoConfigureTestDatabase
@Import({GenreDbStorage.class, GenreRowMapper.class})
public class GenreDbStorageTest {
    @Autowired
    private GenreDbStorage storage;

    @Test
    public void testAddAndGetGenres() {
        Integer expected = 6;
        Assertions.assertEquals(expected, storage.getGenres().size(), "Не корректное количество жанров");
    }

    @Test
    public void testGetGenre() {
        Assertions.assertEquals("Комедия", storage.getGenre(1).getName(), "Не получили жанр по id");
    }
}
