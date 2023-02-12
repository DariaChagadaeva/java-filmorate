package ru.yandex.practicum.filmorate.model;

import lombok.*;
import ru.yandex.practicum.filmorate.exceptions.FilmAlreadyExistException;
import ru.yandex.practicum.filmorate.exceptions.NoSuchUserException;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
public class Film {
    private long id;
    @NotBlank
    private String name;
    @Size(min = 1, max = 200)
    private String description;
    @NotNull
    private LocalDate releaseDate;
    @Positive
    private long duration; //minutes
    private final Set<Long> likes = new HashSet<>();

    public void addLike(long userId) {
        if(likes.contains(userId)) {
            throw new FilmAlreadyExistException(String.format("User %d has already liked", userId));
        }
        likes.add(userId);
    }

    public void deleteLike(long userId) {
        if(!likes.contains(userId)) {
            throw new NoSuchUserException(String.format("User %d didn't like film", userId));
        }
        likes.remove(userId);
    }


}
