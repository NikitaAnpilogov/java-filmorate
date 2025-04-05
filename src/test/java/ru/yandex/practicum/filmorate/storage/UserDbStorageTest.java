package ru.yandex.practicum.filmorate.storage;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.mappers.UserRowMapper;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

@JdbcTest
@AutoConfigureTestDatabase
@Import({UserDbStorage.class, UserRowMapper.class})
public class UserDbStorageTest {
    private User user = new User("abc@mail.ru", "login1", "name1", LocalDate.of(1990, 1, 1));
    private User user2 = new User("abc@email.ru", "login2", "name2", LocalDate.of(1991, 1, 1));
    private User user3 = new User("abc@gmail.ru", "login3", "name3", LocalDate.of(1992, 1, 1));

    @Autowired
    private UserDbStorage storage;

    @Test
    public void testAddAndGetUsers() {
        User test = storage.addUser(user);
        Integer expectedId = 1;
        Assertions.assertEquals(expectedId, test.getId(), "Не добавил пользователя");
        storage.addUser(user2);
        Integer expected = 2;
        Assertions.assertEquals(expected, storage.getUsers().size(), "Не получили список пользователей");
    }

    @Test
    public void testGetUser() {
        storage.addUser(user);
        storage.addUser(user2);
        User test = storage.addUser(user3);
        Assertions.assertEquals(user3.getLogin(), storage.getUser(test.getId()).getLogin(), "Не получили пользователя по id");
    }

    @Test
    public void testUpdateUser() {
        User test = storage.addUser(user);
        test.setLogin("update");
        test = storage.updateUser(test);
        Assertions.assertEquals("update", storage.getUser(test.getId()).getLogin(), "Пользователь не обновился");
    }
}
