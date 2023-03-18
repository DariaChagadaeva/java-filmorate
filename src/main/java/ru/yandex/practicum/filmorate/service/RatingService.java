package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.RatingDao;
import ru.yandex.practicum.filmorate.exceptions.NoSuchMPAException;
import ru.yandex.practicum.filmorate.model.Rating;

import java.util.List;

@Service
public class RatingService {
    private final RatingDao ratingStorage;

    @Autowired
    public RatingService(RatingDao mpaStorage) {
        this.ratingStorage = mpaStorage;
    }

    public List<Rating> getAllRating() {
        return ratingStorage.getAllRating();
    }

    public Rating getRatingById(long id) {
        try {
            return ratingStorage.getRatingById(id);
        } catch (NoSuchMPAException e) {
            throw new NoSuchMPAException("No such rating");

        }
    }
}
