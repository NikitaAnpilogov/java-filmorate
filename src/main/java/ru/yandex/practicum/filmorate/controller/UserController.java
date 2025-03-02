package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
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
        if (user.getEmail().indexOf("@") < 0 || user.getEmail() == null || user.getEmail().isBlank()) {
            log.warn("Электронная почта не может быть пустой и должна содержать символ @");
            throw new ValidationException("Электронная почта не может быть пустой и должна содержать символ @");
        }
        if (user.getLogin().isBlank() || user.getLogin() == null || user.getLogin().indexOf(" ") >= 0) {
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

    @GetMapping
    public Collection<User> getUsers() {
        return users.values();
    }

    @PostMapping
    public User addUser(@RequestBody User user) {
        boolean isLogin = validate(user);
        user.setId(getNextId());
        if (isLogin) {
            user.setName(user.getLogin());
        }
        users.put(user.getId(), user);
        log.info("Валидация прошла, user успешно добавлен");
        return user;
    }

    @PutMapping
    public User updateUser(@RequestBody User user) {
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
            log.info("Валидация прошла, user успешно обновлен");
            return oldUser;
        } else {
            log.warn("Id не найден");
            throw new NotFoundException("Id не найден");
        }
    }
}
