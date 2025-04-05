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

    public Collection<User> addFriend(Integer firsFriend, Integer secondFriend) {// Дружба односторонняя. Для хранений состояния дружбы я использовал мапу
        User firstUser = userStorage.getUser(firsFriend);// Ключ это id другого юзера, значение это статус дружбы. Есть 2 статуса. 1 в друзьях, 2 отправил запрос в друзья.
        User secondUser = userStorage.getUser(secondFriend);// Если пользователи не друзья, то у того кто отправил запрос в мапе появляется второй юзер со статусом отправил запрос
        Optional<Status> checkSecondInFirst = firstUser.checkFriend(secondFriend);// Если второй юзер ответил взаимностью, то статусы у обоих меняются на в друзьях
        Optional<Status> checkFirstInSecond = secondUser.checkFriend(firsFriend);// Если первый пользователь повторно отправляет запрос дружбы, а второй все еще на него не ответил, то ничего не происходит
        if (checkSecondInFirst.isEmpty() && checkFirstInSecond.isEmpty()) {// У удаление из друзей похожая логика, Если пользователи в друзьях, то у первого пользователя второй просто удаляется, а у второго в мапе меняется статус на отправил запрос
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
