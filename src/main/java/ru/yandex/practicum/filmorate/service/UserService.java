package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
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

    public Collection<User> addFriend(Integer firsFriend, Integer secondFriend) {
        User firstUser = userStorage.getUser(firsFriend);
        firstUser.addFriend(secondFriend);
        User secondUser = userStorage.getUser(secondFriend);
        secondUser.addFriend(firsFriend);
        userStorage.updateUser(firstUser);
        userStorage.updateUser(secondUser);
        return List.of(firstUser, secondUser);
    }

    public Collection<User> removeFriend(Integer firsFriend, Integer secondFriend) {
        User firstUser = userStorage.getUser(firsFriend);
        firstUser.removeFriend(secondFriend);
        User secondUser = userStorage.getUser(secondFriend);
        secondUser.removeFriend(firsFriend);
        userStorage.updateUser(firstUser);
        userStorage.updateUser(secondUser);
        return List.of(firstUser, secondUser);
    }

    public Collection<User> getFriends(Integer id) {
        User user = userStorage.getUser(id);
        return user.getFriends().stream()
                .map(userStorage::getUser)
                .collect(Collectors.toList());
    }

    public Collection<User> getFriends(Integer firstId, Integer secondId) {
        if (secondId == null) {
            return getFriends(firstId);
        } else {
            User firstUser = userStorage.getUser(firstId);
            User secondUser = userStorage.getUser(secondId);
            Set<Integer> friendsOfSecondUser = secondUser.getFriends();
            return firstUser.getFriends().stream()
                    .filter(friendsOfSecondUser::contains)
                    .map(userStorage::getUser)
                    .collect(Collectors.toSet());
        }
    }
}
