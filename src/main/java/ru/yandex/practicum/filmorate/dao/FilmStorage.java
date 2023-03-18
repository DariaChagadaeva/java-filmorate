package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {
    Film addNewFilm(Film film);
    Film updateFilm(Film film);
    List<Film> getFilms();
    Film findFilmById(long filmId);

    void addLike(long filmId, long userId);

    void deleteLike(long filmId, long userId);

    List<Film> getMostPopularFilms(long count);
}
