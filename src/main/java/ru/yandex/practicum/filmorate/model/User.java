package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.*;

@Data
@EqualsAndHashCode(of = {"id"})
@NoArgsConstructor
public class User {
    private Integer id;
    private String email;
    private String login;
    private String name;
    private LocalDate birthday;
    private Map<Integer, Status> friends = new HashMap<>();

    public User(Integer id, String email, String login, String name, LocalDate birthday) {
        this.id = id;
        this.email = email;
        this.login = login;
        this.name = name;
        this.birthday = birthday;
    }

    public User(String email, String login, String name, LocalDate birthday) {
        this.email = email;
        this.login = login;
        this.name = name;
        this.birthday = birthday;
    }

    public User(String email, String login, LocalDate birthday) {
        this.email = email;
        this.login = login;
        this.name = login;
        this.birthday = birthday;
    }

    public void addFriend(Integer id, Status status) {
        friends.put(id, status);
    }

    public void removeFriend(Integer id) {
        friends.remove(id);
    }

    public void setStatusFriend(Integer id, Status status) {
        friends.put(id, status);
    }

    public Optional<Status> checkFriend(Integer id) {
        if (friends.containsKey(id)) {
            return Optional.of(friends.get(id));
        } else {
            return Optional.empty();
        }
    }
}
