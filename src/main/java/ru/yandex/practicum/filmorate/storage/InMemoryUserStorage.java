package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Integer, User> users = new HashMap<>();

    private Integer getNextId() {
        int currentMaxId = users.keySet()
                .stream()
                .mapToInt(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    private boolean validate(User user) {
        boolean isLogin = false;
        if (!user.getEmail().contains("@") || user.getEmail().isBlank()) {
            log.warn("Электронная почта не может быть пустой и должна содержать символ @");
            throw new ValidationException("Электронная почта не может быть пустой и должна содержать символ @");
        }
        if (user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            log.warn("Логин не может быть пустым и содержать пробелы");
            throw new ValidationException("Логин не может быть пустым и содержать пробелы");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            isLogin = true;
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.warn("Дата рождения не может быть в будущем");
            throw new ValidationException("Дата рождения не может быть в будущем");
        }
        return isLogin;
    }

    public Collection<User> getUsers() {
        return users.values();
    }

    public User addUser(User user) {
        boolean isLogin = validate(user);
        user.setId(getNextId());
        if (isLogin) {
            user.setName(user.getLogin());
        }
        users.put(user.getId(), user);
        log.info("Валидация прошла, user успешно добавлен");
        return user;
    }

    public User updateUser(User user) {
        if (user.getId() == null) {
            log.warn("Id должен быть указан");
            throw new NotFoundException("Id должен быть указан");
        }
        if (users.containsKey(user.getId())) {
            boolean isLogin = validate(user);
            User oldUser = users.get(user.getId());
            oldUser.setLogin(user.getLogin());
            if (isLogin) {
                oldUser.setName(user.getLogin());
            } else {
                oldUser.setName(user.getName());
            }
            oldUser.setEmail(user.getEmail());
            oldUser.setBirthday(user.getBirthday());
            oldUser.setFriends(user.getFriends());
            log.info("Валидация прошла, user успешно обновлен");
            return oldUser;
        } else {
            log.warn("Id не найден");
            throw new NotFoundException("Id не найден");
        }
    }

    public User getUser(Integer id) {
        if (users.containsKey(id)) {
            return users.get(id);
        } else {
            throw new NotFoundException("User не найден");
        }
    }

    public boolean checkUser(Integer id) {
        if (users.containsKey(id)) {
            return true;
        } else {
            throw new NotFoundException("User не найден");
        }
    }
}
