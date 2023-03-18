package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

public interface GenreDao {
    List<Genre> getAllGenres();
    Genre getGenreById(long genreId);

    void addNewGenreFilm(long filmId, long genreId);

    void deleteGenreFilm(long filmId, long genreId);

    List<Genre> getGenreFilm(long filmId);
}
