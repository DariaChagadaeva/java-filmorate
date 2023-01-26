package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class FilmService {

    private FilmStorage filmStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }


    /**Создайте FilmService, который будет отвечать за операции с фильмами,
     * — добавление и удаление лайка, вывод 10 наиболее популярных фильмов по количеству лайков.
     * Пусть пока каждый пользователь может поставить лайк фильму только один раз.
     */

    public Film addLike(long filmId, long userId) {
        Film film = filmStorage.findFilmById(filmId);
        film.addLike(userId);
        return filmStorage.updateFilm(film);
    }

    public Film deleteLike(long filmId, long userId) {
        Film film = filmStorage.findFilmById(filmId);
        film.deleteLike(userId);
        return filmStorage.updateFilm(film);
    }

    public List<Film> getMostPopularFilms(long count) {
        List<Film> films = filmStorage.getFilms();
        List<Film> mostPopularFilms = films.stream()
                .sorted((o1, o2) -> o2.getLikes().size() - o1.getLikes().size())
                .limit(count)
                .collect(Collectors.toList());
        return mostPopularFilms;
    }

    public Film addNewFilm(Film film) {
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
}
