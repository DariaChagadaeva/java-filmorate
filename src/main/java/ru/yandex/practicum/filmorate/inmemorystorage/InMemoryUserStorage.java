package ru.yandex.practicum.filmorate.inmemorystorage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.UserStorage;
import ru.yandex.practicum.filmorate.exceptions.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Friendship;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Component
public class InMemoryUserStorage implements UserStorage {
    private long newId = 0;
    private final Map<Long, User> users = new HashMap<>();
    @Override
    public User addNewUser(User user) {
        if(user.getName() == null || user.getName() == "") {
            user.setName(user.getLogin());
        }
        final long id = ++newId;
        user.setId(id);
        users.put(id, user);
        return user;
    }

    @Override
    public User updateUser(User user) {
        if(users.get(user.getId()) == null) {
            throw new EntityNotFoundException("No such user exists");
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
    public User findUserById(long userId) {
        if(!users.containsKey(userId)) {
            throw new EntityNotFoundException("User does not exist");
        }
        return users.get(userId);
    }

    @Override
    public void addFriend(long userId, long friendId) {
        User user = findUserById(userId);
        user.addFriend(friendId);
    }

    @Override
    public void deleteFriend(long userId, long friendId) {
        User user = findUserById(userId);
        user.deleteFriend(friendId);
    }

    @Override
    public List<User> getFriendsList(long userId) {
        User user = findUserById(userId);
        List<User> friendsList = new ArrayList<>();
        for(Friendship friends : user.getFriends()) {
            long friendId = friends.getFriendId();
            friendsList.add(findUserById(friendId));
        }
        return friendsList;
    }

    @Override
    public List<User> getFriendsCommonList(long userId, long otherUserId) {
        User user = findUserById(userId);
        User otherUser = findUserById(otherUserId);
        Set<Friendship> userFriends = user.getFriends();
        Set<Friendship> otherUserFriends = otherUser.getFriends();
        List<User> friendsCommonList = new ArrayList<>();
        for (Friendship friend : userFriends) {
            long userFriendId = friend.getFriendId();
            if (otherUserFriends.contains(userFriendId)) {
                friendsCommonList.add(findUserById(userFriendId));
            }
        }
        return friendsCommonList;
    }
}
