package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.exceptions.NoSuchFilmException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    @Autowired
    InMemoryFilmStorage storage;
    private final int MAX_NAME_SIZE = 200;
    private final LocalDate RELEASE_DATE = LocalDate.of(1895, 12, 28);

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        try {
            validateFilm(film);
            log.info("Creating film {}", film);
            return storage.addNewFilm(film);
        } catch (ValidationException e) {
            log.debug(e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        try {
            log.info("Updating film {}", film);
            return storage.updateFilm(film);
        } catch (NoSuchFilmException e) {
            log.debug("The film does not exist");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The film does not exist");
        } catch (ValidationException e) {
            log.debug(e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @GetMapping
    public List<Film> getAll() {
        final List<Film> films = storage.getFilms();
        log.info("Get all films {}", films.size());
        return films;
    }

    void validateFilm(Film film) {
        if(film.getName() == null || film.getName().isEmpty()) {
            throw new ValidationException("Film name invalid");
        }
        if(film.getDescription() != null && film.getDescription().length() > MAX_NAME_SIZE) {
            throw new ValidationException("Film description invalid");
        }
        if(film.getReleaseDate() == null || film.getReleaseDate().isBefore(RELEASE_DATE)) {
            throw new ValidationException("Film release date invalid");
        }
        if(film.getDuration() <= 0) {
            throw new ValidationException("Film duration invalid");
        }
    }
}
