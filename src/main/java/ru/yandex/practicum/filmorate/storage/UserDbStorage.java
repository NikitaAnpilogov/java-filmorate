package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.mappers.UserRowMapper;
import ru.yandex.practicum.filmorate.model.Status;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Repository
@Slf4j
@Qualifier("userDbStorage")
public class UserDbStorage extends BaseStorage<User> implements UserStorage {
    private static final String FIND_ALL_USER_ID_QUERY = "SELECT id FROM users";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM users WHERE id = ?";
    private static final String INSERT_USER_QUERY = "INSERT INTO users (email, login, name, birthday) VALUES (?, ?, ?, ?)";
    private static final String INSERT_USER_FRIENDS_QUERY = "INSERT INTO FRIENDS (SEND_FRIEND_USER_ID, GET_FRIEND_USER_ID, STATUS_ID) " +
            "VALUES ( ?, ?, ( " +
            "SELECT STATUS.ID " +
            "FROM STATUS " +
            "WHERE NAME = ? " +
            "LIMIT 1 ))";
    private static final String UPDATE_QUERY = "UPDATE users SET email = ?, login = ?, name = ?, birthday = ? WHERE id = ?";
    private static final String FIND_ALL_FRIENDS_ID_QUERY = "SELECT get_friend_user_id FROM friends WHERE send_friend_user_id = ?";
    private static final String FIND_ALL_FRIENDS_STATUS_QUERY = "SELECT name FROM friends JOIN status ON friends.STATUS_ID = status.ID WHERE send_friend_user_id = ?";
    private static final String DELETE_FRIENDS_BY_ID_QUERY = "DELETE FROM friends WHERE send_friend_user_id = ?";

    @Autowired
    public UserDbStorage(JdbcTemplate jdbc, UserRowMapper mapper) {
        super(jdbc, mapper);
    }

    @Override
    public Collection<User> getUsers() {
        List<Integer> id = jdbc.queryForList(FIND_ALL_USER_ID_QUERY, Integer.class);
        return id.stream()
                .map(this::getUser)
                .sorted(Comparator.comparing(User::getId))
                .collect(Collectors.toList());
    }

    @Override
    public User addUser(User user) {
        boolean isLogin = validate(user);
        if (isLogin) {
            user.setName(user.getLogin());
        }
        Integer id = insert(INSERT_USER_QUERY, user.getEmail(), user.getLogin(), user.getName(), Timestamp.valueOf(user.getBirthday().atStartOfDay()));
        Map<Integer, Status> friends = user.getFriends();
        for (Integer i : friends.keySet()) {
            insert(INSERT_USER_FRIENDS_QUERY, id, i, String.valueOf(friends.get(i)));
        }
        user.setId(id);
        log.info("Валидация прошла, user успешно добавлен");
        return user;
    }

    @Override
    public User updateUser(User user) {
        if (user.getId() == null) {
            log.warn("Id должен быть указан");
            throw new NotFoundException("Id должен быть указан");
        }
        if (checkUser(user.getId())) {
            boolean isLogin = validate(user);
            if (isLogin) {
                user.setName(user.getLogin());
            }
            update(UPDATE_QUERY, user.getEmail(), user.getLogin(), user.getName(), Timestamp.valueOf(user.getBirthday().atStartOfDay()), user.getId());
            delete(DELETE_FRIENDS_BY_ID_QUERY, user.getId());
            Map<Integer, Status> friends = user.getFriends();
            for (Integer i : friends.keySet()) {
                insert(INSERT_USER_FRIENDS_QUERY, user.getId(), i, String.valueOf(friends.get(i)));
            }
            log.info("Валидация прошла, user успешно обновлен");
            return user;
        } else {
            log.warn("Id не найден");
            throw new NotFoundException("Id не найден");
        }
    }

    @Override
    public User getUser(Integer id) {
        Optional<User> optionalUser = findOne(FIND_BY_ID_QUERY, id);
        User user;
        if (optionalUser.isPresent()) {
            user = optionalUser.get();
        } else {
            throw new NotFoundException("User not found");
        }
        List<Integer> friendsId = jdbc.queryForList(FIND_ALL_FRIENDS_ID_QUERY, Integer.class, user.getId());
        List<String> friendsStatus = jdbc.queryForList(FIND_ALL_FRIENDS_STATUS_QUERY, String.class, user.getId());
        Map<Integer, Status> friends = new HashMap<>();
        for (int i = 0; i < friendsId.size(); i++) {
            friends.put(friendsId.get(i), getStatus(friendsStatus.get(i)));
        }
        user.setFriends(friends);
        return user;
    }

    @Override
    public boolean checkUser(Integer id) {
        Optional<User> optionalUser = findOne(FIND_BY_ID_QUERY, id);
        if (optionalUser.isPresent()) {
            return true;
        } else {
            throw new NotFoundException("User not found");
        }
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

    private Status getStatus(String status) {
        return switch (status) {
            case "IN_FRIENDS" -> Status.IN_FRIENDS;
            case "in_friends" -> Status.IN_FRIENDS;
            case "SENT_REQUEST" -> Status.SENT_REQUEST;
            case "sent_request" -> Status.SENT_REQUEST;
            default -> null;
        };
    }
}