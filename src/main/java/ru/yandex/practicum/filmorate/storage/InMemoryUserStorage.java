package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NoSuchUserException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class InMemoryUserStorage implements UserStorage{
    private int newId = 0;
    private final Map<Integer, User> users = new HashMap<>();
    @Override
    public User addNewUser(User user) {
        if(user.getName() == null) {
            user.setName(user.getLogin());
        }
        final int id = generateId();
        user.setId(id);
        users.put(id, user);
        return user;
    }

    @Override
    public User updateUser(User user) {
        if(users.get(user.getId()) == null) {
            throw new NoSuchUserException("No such user exists");
        } else {
            users.put(user.getId(), user);
            return user;
        }
    }

    @Override
    public List<User> getUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public int generateId() {
        return ++newId;
    }
}
