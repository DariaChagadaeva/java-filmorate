package ru.yandex.practicum.filmorate.inmemorystorage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.FilmStorage;
import ru.yandex.practicum.filmorate.exceptions.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private long id = 0;
    private final Map<Long, Film> films = new HashMap<>();

    @Override
    public Film addNewFilm(Film film) {
        film.setId(++id);
        films.put(id, film);
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        if(films.get(film.getId()) == null) {
            throw new EntityNotFoundException("No such film exists");
        } else {
            films.put(film.getId(), film);
            return film;
        }
    }

    @Override
    public List<Film> getFilms() {
        return new ArrayList<>(films.values());
    }

    @Override
    public Film findFilmById(long filmId) {
        if(!films.containsKey(filmId)) {
            throw new EntityNotFoundException("Film does not exist");
        }
        return films.get(filmId);
    }

    @Override
    public void addLike(long filmId, long userId) {
        Film film = findFilmById(filmId);
        film.addLike(userId);
        updateFilm(film);
    }

    @Override
    public void deleteLike(long filmId, long userId) {
        Film film = findFilmById(filmId);
        film.deleteLike(userId);
        updateFilm(film);
    }

    @Override
    public List<Film> getMostPopularFilms(long count) {
        return null;
    }

}
