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
    public void testAddMpaAndGetListMpa() {
        storage.addMpa(new Mpa(0, "testMpa"));
        storage.addMpa(new Mpa(0, "testMpa2"));
        storage.addMpa(new Mpa(0, "testMpa3"));
        Integer expected = 4;
        Assertions.assertEquals(expected, storage.getListMpa().size(), "Не корректный размер БД");
    }

    @Test
    public void testGetMpa() {
        storage.addMpa(new Mpa(0, "testMpa"));
        Mpa test = storage.addMpa(new Mpa(0, "testMpa2"));
        Assertions.assertEquals("testMpa2", storage.getMpa(test.getId()).getName(), "Не получили Mpa");
    }
}
