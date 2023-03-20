package ru.yandex.practicum.filmorate.model;

import lombok.*;
import ru.yandex.practicum.filmorate.exceptions.EntityAlreadyExistsException;
import ru.yandex.practicum.filmorate.exceptions.EntityNotFoundException;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.time.LocalDate;
import java.util.Set;

@Data
@Builder
public class User {
    private long id;
    private String name;
    @NotBlank
    private String login;
    @Email
    @NotBlank
    private String email;
    private LocalDate birthday;
    private Set<Friendship> friends;

    public void addFriend(long friendId) {
        if(friends.contains(friendId)) {
            throw new EntityAlreadyExistsException("This user has already added to your friends list");
        }
        friends.add(Friendship.builder().friendId(friendId).status(false).build());
    }

    public void deleteFriend(long friendId) {
        if(!friends.contains(friendId)) {
            throw new EntityNotFoundException("This user isn't in your friends list");
        }
        friends.remove(friendId);
    }
}
