package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import java.time.LocalDate;
import java.util.Collection;

public class UserControllerTest {
    User normalUser = new User(1, "abc@mail.ru", "login1", "name1", LocalDate.of(2000, 1, 1));
    User normalUserWithoutName = new User(1, "abc@mail.ru", "login1", "", LocalDate.of(2000, 1, 1));
    User emptyEmail = new User(1, "", "login2", "name2", LocalDate.of(2000, 1, 1));
    User emailWithoutA = new User(1, "abcmail.ru", "login3", "name3", LocalDate.of(2000, 1, 1));
    User emptyLogin = new User(1, "abc@mail.ru", "", "name4", LocalDate.of(2000, 1, 1));
    User loginWithSpace = new User(1, "abc@mail.ru", "log in 5", "name5", LocalDate.of(2000, 1, 1));
    User futureBirthday = new User(1, "abc@mail.ru", "login6", "name6", LocalDate.of(2050, 1, 1));
    User userWithoutId = new User("abc@mail.ru", "login7", "name7", LocalDate.of(2000, 1, 1));
    User notFoundId = new User(45, "abc@mail.ru", "login8", "name8", LocalDate.of(2000, 1, 1));
    UserController userController;

    @BeforeEach
    void start() { // Инициализирую перед каждым тестом, чтобы обнулить контроллер и тесты не зависели друг от друга
        userController = new UserController();
    }

    @Test
    void shouldAddUser() {
        userController.addUser(normalUser);
        Collection<User> collection = userController.getUsers();
        Assertions.assertEquals(1, collection.size(), "User не добавился");
    }

    @Test
    void shouldAddUserWithoutName() {
        userController.addUser(normalUserWithoutName);
        Collection<User> collection = userController.getUsers();
        Assertions.assertEquals(1, collection.size(), "User без имени не добавился");
    }

    @Test
    void testEmptyEmail() {
        Assertions.assertThrows(ValidationException.class, () -> userController.addUser(emptyEmail), "Валидация для пустого email не прошла");
    }

    @Test
    void testEmailWithoutA() {
        Assertions.assertThrows(ValidationException.class, () -> userController.addUser(emailWithoutA), "Валидация для email без @ не прошла");
    }

    @Test
    void testEmptyLogin() {
        Assertions.assertThrows(ValidationException.class, () -> userController.addUser(emptyLogin), "Валидация для пустого Login не прошла");
    }

    @Test
    void testLoginWithSpace() {
        Assertions.assertThrows(ValidationException.class, () -> userController.addUser(loginWithSpace), "Валидация для Login с пробелами не прошла");
    }

    @Test
    void testFutureBirthday() {
        Assertions.assertThrows(ValidationException.class, () -> userController.addUser(futureBirthday), "Валидация для дня рождения не прошла");
    }

    @Test
    void testUpdateEmptyEmail() {
        userController.addUser(normalUser);
        Assertions.assertThrows(ValidationException.class, () -> userController.addUser(emptyEmail), "Валидация для пустого email не прошла");
    }

    @Test
    void testUpdateEmailWithoutA() {
        userController.addUser(normalUser);
        Assertions.assertThrows(ValidationException.class, () -> userController.addUser(emailWithoutA), "Валидация для email без @ не прошла");
    }

    @Test
    void testUpdateEmptyLogin() {
        userController.addUser(normalUser);
        Assertions.assertThrows(ValidationException.class, () -> userController.addUser(emptyLogin), "Валидация для пустого Login не прошла");
    }

    @Test
    void testUpdateLoginWithSpace() {
        userController.addUser(normalUser);
        Assertions.assertThrows(ValidationException.class, () -> userController.addUser(loginWithSpace), "Валидация для Login с пробелами не прошла");
    }

    @Test
    void testUpdateFutureBirthday() {
        userController.addUser(normalUser);
        Assertions.assertThrows(ValidationException.class, () -> userController.addUser(futureBirthday), "Валидация для дня рождения не прошла");
    }

    @Test
    void shouldGetUsers() {
        userController.addUser(normalUser);
        userController.addUser(normalUserWithoutName);
        Collection<User> collection = userController.getUsers();
        Assertions.assertEquals(2, collection.size(), "Не получили список users");
    }

    @Test
    void testUpdateWithoutId() {
        userController.addUser(normalUser);
        Assertions.assertThrows(NotFoundException.class, () -> userController.updateUser(userWithoutId), "Валидация для user без ID не прошла");
    }

    @Test
    void testNotFoundId() {
        userController.addUser(normalUser);
        Assertions.assertThrows(NotFoundException.class, () -> userController.updateUser(notFoundId), "Валидация поиска ID не прошла");
    }
}
