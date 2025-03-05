package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.time.LocalDate;
import java.util.Collection;

public class UserControllerTest {
    User normalUser = new User(1, "abc@mail.ru", "login1", "name1", LocalDate.of(2000, 1, 1));
    User normalUser2 = new User(1, "abcd@mail.ru", "login2", "name2", LocalDate.of(2000, 1, 1));
    User normalUser3 = new User(1, "abcde@mail.ru", "login3", "name3", LocalDate.of(2000, 1, 1));
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
        userController = new UserController(new UserService(new InMemoryUserStorage()));
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

    @Test
    void shouldAddAndGetFriend() {
        userController.addUser(normalUser);
        userController.addUser(normalUser2);
        userController.addFriend(normalUser.getId(), normalUser2.getId());
        Collection<User> friends = userController.getFriendsSecondPath(1, null);
        Assertions.assertEquals(1, friends.size(), "addFriend or getFriends не работает");
    }

    @Test
    void testUnknownFriend() {
        Assertions.assertThrows(NotFoundException.class, () -> userController.addFriend(4, 5), "Валидация добавления в друзья не прошла");
    }

    @Test
    void shouldRemoveFriend() {
        userController.addUser(normalUser);
        userController.addUser(normalUser2);
        userController.addFriend(1, 2);
        userController.removeFriend(1, 2);
        Collection<User> friends = userController.getFriendsSecondPath(1, null);
        Assertions.assertEquals(0, friends.size(), "removeFriend не работает");
    }

    @Test
    void testRemoveUnknownFriend() {
        userController.addUser(normalUser);
        userController.addUser(normalUser2);
        userController.addFriend(normalUser.getId(), normalUser2.getId());
        Assertions.assertThrows(NotFoundException.class, () -> userController.removeFriend(normalUser.getId(), 5), "Валидация удаления из друзей не прошла");
    }

    @Test
    void testRemoveUnknownFriend2() {
        Assertions.assertThrows(NotFoundException.class, () -> userController.removeFriend(normalUser.getId(), 5), "Валидация удаления из друзей не прошла");
    }

    @Test
    void shouldGetFriends() {
        userController.addUser(normalUser);
        userController.addUser(normalUser2);
        userController.addUser(normalUser3);
        userController.addFriend(normalUser.getId(), normalUser3.getId());
        userController.addFriend(normalUser2.getId(), normalUser3.getId());
        Collection<User> friends = userController.getFriends(normalUser.getId(), normalUser2.getId());
        Assertions.assertEquals(1, friends.size(), "getFriends не работает");
    }

    @Test
    void testGetFriends() {
        userController.addUser(normalUser);
        userController.addUser(normalUser2);
        userController.addFriend(normalUser.getId(), normalUser2.getId());
        Assertions.assertThrows(NotFoundException.class, () -> userController.getFriends(normalUser.getId(), 5), "Валидация получения общих друзей не прошла");
    }
}
