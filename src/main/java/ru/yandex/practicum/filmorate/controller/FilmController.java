package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
            filmService.validateFilm(film);
            log.info("Creating film {}", film);
            return filmService.addNewFilm(film);
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
            log.info("Updating film {}", film);
            return filmService.updateFilm(film);
    }

    @GetMapping
    public List<Film> getAll() {
        final List<Film> films = filmService.getFilms();
        log.info("Get all films {}", films.size());
        return films;
    }

    @GetMapping("/{id}")
    public Film getFilmById(@PathVariable("id") long filmId) {
        return filmService.findFilmById(filmId);
    }

    @PutMapping("/{id}/like/{userId}")
    public Film addLike(@PathVariable("id") long filmId, @PathVariable long userId) {
        return filmService.addLike(filmId, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public Film deleteLike(@PathVariable("id") long filmId, @PathVariable long userId) {
        return filmService.deleteLike(filmId, userId);
    }

    @GetMapping("/popular")
    public List<Film> getPopularFilmsListWithCount(@RequestParam(defaultValue = "10", required = false) long count) {
            return filmService.getMostPopularFilms(count);
    }
}
