package ru.yandex.practicum.filmorate.model;

import lombok.*;
import ru.yandex.practicum.filmorate.exceptions.NoSuchUserException;
import ru.yandex.practicum.filmorate.exceptions.UserAlreadyExistException;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
public class User {
    private long id;
    @Email
    @NotBlank
    private String email;
    @NotBlank
    private String login;
    private String name;
    private LocalDate birthday;
    private final Set<Long> friends = new HashSet<>();

    public void addFriend(long friendId) {
        if(friends.contains(friendId)) {
            throw new UserAlreadyExistException("This user has already added to your friends list");
        }
        friends.add(friendId);
    }

    public void deleteFriend(long friendId) {
        if(!friends.contains(friendId)) {
            throw new NoSuchUserException("This user isn't in your friends list");
        }
        friends.remove(friendId);
    }

}
