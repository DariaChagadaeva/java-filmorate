package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {
    User addNewUser(User user);
    User updateUser(User user);
    List<User> getUsers();
    long generateId();
    User findUserById(long userId);
}
