package ru.yandex.practicum.filmorate.dao.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.UserStorage;
import ru.yandex.practicum.filmorate.exceptions.NoSuchUserException;
import ru.yandex.practicum.filmorate.model.*;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Primary
public class UserDbStorage implements UserStorage {
    private final Logger log = LoggerFactory.getLogger(UserDbStorage.class);
    private final JdbcTemplate jdbcTemplate;

    public UserDbStorage(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate=jdbcTemplate;
    }

    @Override
    public User addNewUser(User user) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement pr = con.prepareStatement("INSERT INTO users " +
                    "(name, login, email, birthday) " +
                    "values(?, ?, ?, ?)", new String[]{"user_id"});
            pr.setString(1, user.getName());
            pr.setString(2, user.getLogin());
            pr.setString(3, user.getEmail());
            pr.setDate(4, Date.valueOf(user.getBirthday()));
            return pr;
        }, keyHolder);
        long filmId = Objects.requireNonNull(keyHolder.getKey().longValue());
        user.setId(filmId);
        log.debug("New user was added into the database: {}", user);
        return user;
    }

    @Override
    public User updateUser(User user) {
        if (!getUsers().stream().map(User::getId).collect(Collectors.toSet()).contains(user.getId())) {
            throw new NoSuchUserException("No such user");
        } else {
        jdbcTemplate.update("UPDATE users SET name = ?, login = ?, email = ?, birthday = ? " +
                "WHERE user_id = ?", user.getName(), user.getLogin(), user.getEmail(), user.getBirthday(), user.getId());
        user.setFriends(new HashSet<>(getUserFriends(user.getId())));
        return user;
        }
    }

    @Override
    public List<User> getUsers() {
        return jdbcTemplate.query("SELECT * FROM users", (rs, rowNum) -> makeUser(rs));
    }


    @Override
    public User findUserById(long userId) {
        return jdbcTemplate.query("SELECT * FROM users WHERE user_id = ?", (rs, rowNum) -> makeUser(rs), userId)
                .stream().findFirst().orElseThrow(() -> {throw new NoSuchUserException("No such user");
                });
    }

    @Override
    public void addFriend(long userId, long friendId) {
        List<User> friends = getFriendsList(friendId);
        if(friends.contains(userId)) {
            jdbcTemplate.update("UPDATE friendship SET status = ? " +
                    "WHERE user_id = ? AND friend_id = ?", true, friendId, userId);
        } else {
            jdbcTemplate.update("INSERT INTO friendship (user_id, friend_id, status) " +
                    "values (?, ?, ?)", userId, friendId, false);
        }
    }

    @Override
    public void deleteFriend(long userId, long friendId) {
        jdbcTemplate.update("DELETE FROM friendship WHERE user_id = ? AND friend_id = ?", userId, friendId);
    }

    @Override
    public List<User> getFriendsList(long userId) {
        return jdbcTemplate.query("SELECT * " +
                "FROM users u " +
                "WHERE u.user_id IN " +
                "(SELECT f.friend_id FROM friendship f WHERE f.user_id = ? AND f.status = true " +
                "union SELECT f.user_id FROM friendship f WHERE f.friend_id = ? AND f.status = true " +
                "union SELECT f.friend_id FROM friendship f WHERE f.user_id = ? AND f.status = false)",
                (rs, rowNum) -> makeUser(rs), userId, userId, userId);
    }

    @Override
    public List<User> getFriendsCommonList(long userId, long otherUserId) {
        return jdbcTemplate.query("SELECT * " +
                "FROM users u " +
                "WHERE u.user_id IN " +
                "(SELECT fr.user_id FROM " +
                "(SELECT f.user_id FROM friendship f WHERE (f.friend_id = ? or f.friend_id = ?) " +
                "union all SELECT f.friend_id FROM friendship f WHERE (f.user_id = ? or f.user_id = ?)) fr " +
                "GROUP BY fr.user_id " +
                "HAVING count(*) > 1)", (rs, rowNum) -> makeUser(rs), userId, otherUserId, userId, otherUserId);
    }

    private User makeUser(ResultSet rs) throws SQLException {
        Long id = rs.getLong("user_id");
        User user = User.builder()
                .id(id)
                .name(rs.getString("name"))
                .login(rs.getString("login"))
                .email(rs.getString("email"))
                .birthday(rs.getDate("birthday").toLocalDate())
                .build();
        Set<Friendship> friends = new HashSet<>(getUserFriends(user.getId()));
        user.setFriends(friends);
        return user;
    }

    private List<Friendship> getUserFriends (long userId) {
        return jdbcTemplate.query("SELECT f.friend_id, f.status " +
                "FROM friendship f WHERE f.user_id = ? AND f.status = true " +
                "union SELECT f.user_id, f.status " +
                "FROM friendship f WHERE f.friend_id = ? AND f.status = true " +
                "union SELECT f.user_id, f.status " +
                "FROM friendship f WHERE f.friend_id = ? AND f.status = false",
                (rs, rowNum) -> makeFriendship(rs), userId, userId, userId);
    }

    private Friendship makeFriendship(ResultSet rs) throws SQLException {
        return Friendship.builder()
                .friendId(rs.getInt("friend_id"))
                .status(rs.getBoolean("status"))
                .build();
    }
}
