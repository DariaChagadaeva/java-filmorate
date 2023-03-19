package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.UserStorage;
import ru.yandex.practicum.filmorate.exceptions.EntityNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.dao.FilmStorage;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    private final LocalDate RELEASE_DATE = LocalDate.of(1895, 12, 28);

    public List<Film> getMostPopularFilms(long count) {
        List<Film> films = filmStorage.getFilms();
        List<Film> mostPopularFilms = films.stream()
                .sorted((o1, o2) -> o2.getLikes().size() - o1.getLikes().size())
                .limit(count)
                .collect(Collectors.toList());
        return mostPopularFilms;
    }

    public Film addNewFilm(Film film) {
        validateFilm(film);
        return filmStorage.addNewFilm(film);
    }

    public Film updateFilm(Film film) {
        return filmStorage.updateFilm(film);
    }

    public List<Film> getFilms() {
        return filmStorage.getFilms();
    }

    public Film findFilmById(long filmId) {
        return filmStorage.findFilmById(filmId);
    }

    public void addLike(long filmId, long userId) {
        filmStorage.addLike(filmId, userId);
    }

    public void deleteLike(long filmId, long userId) {
        if(userStorage.findUserById(userId).equals(null)) {
            throw new EntityNotFoundException("No such user");
        }
        if(filmStorage.findFilmById(filmId).equals(null)) {
            throw new EntityNotFoundException("No such film");
        }
        filmStorage.deleteLike(filmId, userId);
    }

    private void validateFilm(Film film) {
        if(film.getReleaseDate() == null || film.getReleaseDate().isBefore(RELEASE_DATE)) {
            throw new ValidationException("Film release date invalid");
        }
    }
}
