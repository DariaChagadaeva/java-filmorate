package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Rating;

import java.util.List;

public interface RatingDao {
    List<Rating> getAllRating();
    Rating getRatingById(long id);
}
