package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.GenreDao;
import ru.yandex.practicum.filmorate.exceptions.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

@Service
public class GenreService {
    private final GenreDao genreStorage;

    @Autowired
    public GenreService(GenreDao genreStorage) {
        this.genreStorage = genreStorage;
    }

    public List<Genre> getAllGenres() {
        return genreStorage.getAllGenres();
    }
    public Genre getGenreById(long genreId){
        try {
            return genreStorage.getGenreById(genreId);
        } catch (EntityNotFoundException e) {
            throw new EntityNotFoundException("No such genre");
        }
    }
}
