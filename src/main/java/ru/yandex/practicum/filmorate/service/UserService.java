package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class UserService {

    private UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    /**Создайте UserService, который будет отвечать за такие операции с пользователями,
     * как добавление в друзья, удаление из друзей, вывод списка общих друзей.
     * Пока пользователям не надо одобрять заявки в друзья — добавляем сразу.
     * То есть если Лена стала другом Саши, то это значит, что Саша теперь друг Лены.
     */

    public User addFriend(long userId, long friendId) {
        User user = userStorage.findUserById(userId);
        User friend = userStorage.findUserById(friendId);
        user.addFriend(friendId);
        friend.addFriend(userId);
        userStorage.updateUser(friend);
        return userStorage.updateUser(user);
    }

    public User deleteFriend(long userId, long friendId) {
        User user = userStorage.findUserById(userId);
        user.deleteFriend(friendId);
        User friend = userStorage.findUserById(friendId);
        friend.deleteFriend(userId);
        return userStorage.updateUser(user);
    }

    public List<User> getFriendsList(long userId) {
        User user = userStorage.findUserById(userId);
        List<User> friendsList = new ArrayList<>();
        for(Long friendId : user.getFriends()) {
            friendsList.add(userStorage.findUserById(friendId));
        }
        return friendsList;
    }

    public List<User> getFriendsCommonList(long userId, long otherUserId) {
        User user = userStorage.findUserById(userId);
        User otherUser = userStorage.findUserById(otherUserId);
        Set<Long> userFriends = user.getFriends();
        Set<Long> otherUserFriends = otherUser.getFriends();
        List<User> friendsCommonList = new ArrayList<>();
        for (Long userFriendId : userFriends) {
            if (otherUserFriends.contains(userFriendId)) {
                friendsCommonList.add(userStorage.findUserById(userFriendId));
            }
        }
        return friendsCommonList;
    }

    public User addNewUser(User user) {
        return userStorage.addNewUser(user);
    }

    public User updateUser(User user) {
        return userStorage.updateUser(user);
    }

    public List<User> getUsers() {
        return userStorage.getUsers();
    }

    public User findUserById(long userId) {
        return userStorage.findUserById(userId);
    }
}
