package ru.yandex.practicum.filmorate.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.GenreDao;
import ru.yandex.practicum.filmorate.exceptions.NoSuchGenreException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Component
public class GenreDaoImpl implements GenreDao {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public GenreDaoImpl(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate=jdbcTemplate;
    }

    @Override
    public List<Genre> getAllGenres() {
        return jdbcTemplate.query("select * from genre", (rs, rowNum) -> makeGenre(rs));
    }

    @Override
    public Genre getGenreById(long genreId) {
        return jdbcTemplate.query("select * from genre where genre_id = ?",
                        (rs, rowNum) -> makeGenre(rs), genreId).stream().findFirst().orElseThrow(() -> {
                    throw new NoSuchGenreException("No such genre");
                });
    }

    @Override
    public void addNewGenreFilm(long filmId, long genreId) {
        jdbcTemplate.update("INSERT INTO film_genre values(?, ?)", filmId, genreId);
    }

    @Override
    public void deleteGenreFilm(long filmId, long genreId) {
        jdbcTemplate.update("DELETE FROM film_genre WHERE film_id = ? AND genre_id = ?", filmId, genreId);
    }

    @Override
    public List<Genre> getGenreFilm(long filmId) {
        return jdbcTemplate.query("SELECT * " +
                "FROM genre " +
                "JOIN film_genre using(genre_id) WHERE film_id = ?", (rs, rowNum) -> makeGenre(rs), filmId);
    }

    private Genre makeGenre(ResultSet rs) throws SQLException {
        return Genre.builder()
                .id(rs.getInt("genre_id"))
                .name(rs.getString("name"))
                .build();
    }
}
