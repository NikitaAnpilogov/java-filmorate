package ru.yandex.practicum.filmorate.storage;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.mappers.MpaRowMapper;
import ru.yandex.practicum.filmorate.model.Mpa;

@JdbcTest
@AutoConfigureTestDatabase
@Import({MpaDbStorage.class, MpaRowMapper.class})
public class MpaDbStorageTest {
    @Autowired
    private MpaDbStorage storage;

    @Test
    public void testGetListMpa() {
        Integer expected = 5;
        Assertions.assertEquals(expected, storage.getListMpa().size(), "Не корректный размер БД");
    }

    @Test
    public void testGetMpa() {
        Assertions.assertEquals("G", storage.getMpa(1).getName(), "Не получили Mpa");
    }
}
