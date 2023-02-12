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
    private long newId = 0;
    private final Map<Long, User> users = new HashMap<>();
    @Override
    public User addNewUser(User user) {
        if(user.getName() == null || user.getName() == "") {
            user.setName(user.getLogin());
        }
        final long id = generateId();
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
    public long generateId() {
        return ++newId;
    }

    @Override
    public User findUserById(long userId) {
        if(!users.containsKey(userId)) {
            throw new NoSuchUserException("User does not exist");
        }
        return users.get(userId);
    }
}
