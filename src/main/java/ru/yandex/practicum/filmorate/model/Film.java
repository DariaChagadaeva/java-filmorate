package ru.yandex.practicum.filmorate.model;

import lombok.*;
import ru.yandex.practicum.filmorate.exceptions.FilmAlreadyExistException;
import ru.yandex.practicum.filmorate.exceptions.GenreAlreadyExistException;
import ru.yandex.practicum.filmorate.exceptions.NoSuchUserException;

import javax.validation.constraints.*;
import java.time.LocalDate;
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
    private Rating mpa;
    private Set<Genre> genres;
    private Set<Long> likes;

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

    public void addGenre(Genre genre) {
        if(genres.contains(genre.getId())) {
            throw new GenreAlreadyExistException("Genre has already exist");
        }
        genres.add(genre);
    }

    public void deleteGenre(Genre genre) {
        if(!genres.contains(genre.getId())) {
            throw new GenreAlreadyExistException("Genre has already remove");
        }
        genres.remove(genre);
    }
}
