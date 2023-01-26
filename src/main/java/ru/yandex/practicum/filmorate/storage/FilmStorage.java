package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {
    Film addNewFilm(Film film);
    Film updateFilm(Film film);
    List<Film> getFilms();
    Film findFilmById(long filmId);
}
