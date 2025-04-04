package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Status;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(@Qualifier("userDbStorage") UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public Collection<User> getUsers() {
        return userStorage.getUsers();
    }

    public User addUser(User user) {
        return userStorage.addUser(user);
    }

    public User updateUser(User user) {
        return userStorage.updateUser(user);
    }

    public User getUser(Integer id) {
        return userStorage.getUser(id);
    }

    public Collection<User> addFriend(Integer firsFriend, Integer secondFriend) {
        User firstUser = userStorage.getUser(firsFriend);
        User secondUser = userStorage.getUser(secondFriend);
        Optional<Status> checkSecondInFirst = firstUser.checkFriend(secondFriend);
        Optional<Status> checkFirstInSecond = secondUser.checkFriend(firsFriend);
        if (checkSecondInFirst.isEmpty() && checkFirstInSecond.isEmpty()) {
            firstUser.addFriend(secondFriend, Status.SENT_REQUEST);
        } else if (checkSecondInFirst.isEmpty() && checkFirstInSecond.isPresent()) {
            firstUser.addFriend(secondFriend, Status.IN_FRIENDS);
            secondUser.addFriend(firsFriend, Status.IN_FRIENDS);
        }
        userStorage.updateUser(firstUser);
        userStorage.updateUser(secondUser);
        return List.of(firstUser, secondUser);
    }

    public Collection<User> removeFriend(Integer firsFriend, Integer secondFriend) {
        User firstUser = userStorage.getUser(firsFriend);
        User secondUser = userStorage.getUser(secondFriend);
        Optional<Status> checkSecondInFirst = firstUser.checkFriend(secondFriend);
        Optional<Status> checkFirstInSecond = secondUser.checkFriend(firsFriend);
        if (checkSecondInFirst.isPresent() && checkFirstInSecond.isPresent()) {
            firstUser.removeFriend(secondFriend);
            secondUser.setStatusFriend(firsFriend, Status.SENT_REQUEST);
        } else if (checkSecondInFirst.isPresent() && checkFirstInSecond.isEmpty()) {
            firstUser.removeFriend(secondFriend);
        }
        userStorage.updateUser(firstUser);
        userStorage.updateUser(secondUser);
        return List.of(firstUser, secondUser);
    }

    public Collection<User> getFriends(Integer id) {
        User user = userStorage.getUser(id);
        return user.getFriends().keySet().stream()
                .map(userStorage::getUser)
                .collect(Collectors.toList());
    }

    public Collection<User> getFriends(Integer firstId, Integer secondId) {
        if (secondId == null) {
            return getFriends(firstId);
        } else {
            User firstUser = userStorage.getUser(firstId);
            User secondUser = userStorage.getUser(secondId);
            Map<Integer, Status> friendsOfSecondUser = secondUser.getFriends();
            return firstUser.getFriends().keySet().stream()
                    .filter(friendsOfSecondUser::containsKey)
                    .map(userStorage::getUser)
                    .collect(Collectors.toSet());
        }
    }
}
