package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NoSuchUserException;
import ru.yandex.practicum.filmorate.exceptions.UserAlreadyExistException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.dao.UserStorage;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public void addFriend(long userId, long friendId) {
        try {
            userStorage.findUserById(userId);
            userStorage.findUserById(friendId);
            userStorage.addFriend(userId, friendId);
            userStorage.updateUser(userStorage.findUserById(userId));
        } catch (NoSuchUserException e) {
            throw new NoSuchUserException("No such user");
        }
    }

    public void deleteFriend(long userId, long friendId) {
        try {
            userStorage.findUserById(userId);
            userStorage.findUserById(friendId);
            userStorage.deleteFriend(userId, friendId);
            userStorage.updateUser(userStorage.findUserById(userId));
        } catch (NoSuchUserException e) {
            throw new NoSuchUserException("No such user");
        }
    }

    public List<User> getFriendsList(long userId) {
        try {
            userStorage.findUserById(userId);
            return userStorage.getFriendsList(userId);
        } catch (NoSuchUserException e) {
            throw new NoSuchUserException("No such user");
        }
    }

    public List<User> getFriendsCommonList(long userId, long otherUserId) {
        try {
            userStorage.findUserById(userId);
            return userStorage.getFriendsCommonList(userId, otherUserId);
        } catch (NoSuchUserException e) {
            throw new NoSuchUserException("No such user");
        }
    }

    public User addNewUser(User user) {
        if(user.getName() == null || user.getName() == "") {
            user.setName(user.getLogin());
        }
        validateUser(user);
        if(user.getFriends() == null) {
            user.setFriends(new HashSet<>());
        }
        if(userStorage.getUsers().stream().map(User::getId).collect(Collectors.toSet()).contains(user.getId())) {
            throw new UserAlreadyExistException("User is already exist");
        }
        return userStorage.addNewUser(user);
    }

    public User updateUser(User user) {
        return userStorage.updateUser(user);
    }

    public List<User> getUsers() {
        return userStorage.getUsers();
    }

    public User findUserById(long userId) {
        try {
            return userStorage.findUserById(userId);
        } catch (NoSuchUserException e) {
            throw new NoSuchUserException("No such user");
        }
    }

    private void validateUser(User user) {
        if(user.getLogin().contains(" ")) {
            throw new ValidationException("User login invalid");
        }
        if(user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException("User birthday invalid");
        }
    }
}
