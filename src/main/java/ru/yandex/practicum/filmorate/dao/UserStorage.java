package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {
    User addNewUser(User user);
    User updateUser(User user);
    List<User> getUsers();
    User findUserById(long userId);

    void addFriend(long userId, long friendId);

    void deleteFriend(long userId, long friendId);

    List<User> getFriendsList(long userId);

    List<User> getFriendsCommonList(long userId, long otherUserId);
}
