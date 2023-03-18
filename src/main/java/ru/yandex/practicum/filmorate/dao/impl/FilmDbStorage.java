package ru.yandex.practicum.filmorate.dao.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.FilmStorage;
import ru.yandex.practicum.filmorate.dao.GenreDao;
import ru.yandex.practicum.filmorate.dao.RatingDao;
import ru.yandex.practicum.filmorate.exceptions.FilmAlreadyExistException;
import ru.yandex.practicum.filmorate.exceptions.NoSuchFilmException;
import ru.yandex.practicum.filmorate.exceptions.NoSuchUserException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Primary
public class FilmDbStorage implements FilmStorage {
    private final Logger log = LoggerFactory.getLogger(UserDbStorage.class);
    private final JdbcTemplate jdbcTemplate;
    private final RatingDao ratingStorage;
    private final GenreDao genreStorage;

    public FilmDbStorage(JdbcTemplate jdbcTemplate, RatingDao ratingStorage, GenreDao genreStorage){
        this.jdbcTemplate=jdbcTemplate;
        this.ratingStorage = ratingStorage;
        this.genreStorage = genreStorage;
    }



    @Override
    public Film addNewFilm(Film film) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement pr = con.prepareStatement("INSERT INTO films " +
                    "(name, description, release_date, duration, rating_id) " +
                    "values(?, ?, ?, ?, ?)", new String[]{"film_id"});
            pr.setString(1, film.getName());
            pr.setString(2, film.getDescription());
            pr.setDate(3, Date.valueOf(film.getReleaseDate()));
            pr.setLong(4, film.getDuration());
            pr.setLong(5, ratingStorage.getRatingById(film.getMpa().getId()).getId());
            return pr;
            }, keyHolder);
        long filmId = Objects.requireNonNull(keyHolder.getKey().longValue());
        if(film.getGenres() != null) {
            film.getGenres().forEach(genre ->
                    genreStorage.addNewGenreFilm(filmId, genreStorage.getGenreById(genre.getId()).getId()));
        }
        film.setId(filmId);
        log.debug("New film was added into the database: {}", film);
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        if(!getFilms().stream().map(Film::getId).collect(Collectors.toSet()).contains(film.getId())) {
            throw new NoSuchFilmException("No such film");
        } else {
            jdbcTemplate.update("UPDATE films " +
                            "SET name = ?, description = ?, release_date = ?, duration = ?, " +
                            "rating_id = ? " +
                            "WHERE film_id = ?", film.getName(), film.getDescription(), film.getReleaseDate()
                    , film.getDuration(), ratingStorage.getRatingById(film.getMpa().getId()).getId()
                    , film.getId());
            if (film.getGenres() != null) {
                List<Genre> filmGenres = genreStorage.getGenreFilm(film.getId());
                film.getGenres().stream().filter(genre -> {
                            return !filmGenres.stream().map(Genre::getId).collect(Collectors.toSet()).contains(genre.getId());
                        })
                        .forEach(genre -> genreStorage.addNewGenreFilm(film.getId()
                                , genreStorage.getGenreById(genre.getId()).getId()));

                filmGenres.stream().filter(genre -> !film.getGenres().stream().map(Genre::getId)
                        .collect(Collectors.toSet()).contains(genre.getId())).forEach((genre) ->
                        genreStorage.deleteGenreFilm(film.getId(), genre.getId()));
            }
            film.setLikes(new HashSet<>(getFilmLikes(film.getId())));
            log.debug("Film was was updated: {}", film);
            return findFilmById(film.getId());
        }
    }

    @Override
    public List<Film> getFilms() {
        String sqlQuery = "SELECT f.film_id, " +
                "f.name, " +
                "f.description, " +
                "f.release_date, " +
                "f.duration, " +
                "r.rating_id AS rating_id, " +
                "r.name AS rating_name "+
                "FROM films f " +
                "JOIN rating r using (rating_id)";
        return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> makeFilm(rs));
    }

    @Override
    public Film findFilmById(long filmId) {
        return jdbcTemplate.query(
                        "SELECT f.film_id, " +
                                "f.name, " +
                                "f.description, " +
                                "f.release_date, " +
                                "f.duration, " +
                                "r.rating_id AS rating_id, " +
                                "r.name AS rating_name "+
                                "FROM films f " +
                                "JOIN rating r using(rating_id) " +
                                "WHERE film_id = ?"
                , (rs, rowNum) -> makeFilm(rs), filmId)
                .stream().findFirst().orElseThrow(() -> {throw new NoSuchFilmException("No such film");
        });
    }

    @Override
    public void addLike(long filmId, long userId) {
        if(getFilmLikes(filmId).contains(userId)) {
            throw new FilmAlreadyExistException("User has already liked this film");
        } else {
            jdbcTemplate.update("INSERT INTO film_likes values(?, ?)", filmId, userId);
        }
    }

    @Override
    public void deleteLike(long filmId, long userId) {
            jdbcTemplate.update("DELETE FROM film_likes WHERE film_id = ? AND user_id = ?", filmId, userId);
    }

    @Override
    public List<Film> getMostPopularFilms(long count) {
        return jdbcTemplate.query("SELECT f.film_id, f.name, f.description, f.release_date, " +
                "f.duration, r.rating_id AS rating_id, r.name AS rating_name " +
                "FROM films f " +
                "INNER JOIN rating r USING (rating_id) " +
                "LEFT JOIN film_likes fl USING (film_id) " +
                "GROUP BY f.film_id " +
                "ORDER BY count(distinct fl.user_id) DESC " +
                "LIMIT ?" , (rs, rowNum) -> makeFilm(rs), count);
    }

    private Film makeFilm(ResultSet rs) throws SQLException {
        long id = rs.getLong("film_id");
        LocalDate releaseDate = rs.getDate("release_date").toLocalDate();
        Film film = Film.builder()
                .id(id)
                .name(rs.getString("name"))
                .description(rs.getString("description"))
                .releaseDate(releaseDate)
                .duration(rs.getLong("duration"))
                .mpa(Rating.builder()
                        .id(rs.getInt("rating_id"))
                        .name(rs.getString("rating_name"))
                        .build())
                .build();
        Set<Genre> genres = new HashSet<>(genreStorage.getGenreFilm(film.getId()));
        Set<Long> likes = new HashSet<>(getFilmLikes(film.getId()));
        film.setGenres(genres);
        film.setLikes(likes);
        return film;
    }

    private Set<Long> getFilmLikes(long id) {
        Set<Long> usersLikesId = new HashSet<>();
        SqlRowSet likeRow = jdbcTemplate.queryForRowSet("select user_id from film_likes " +
                "where film_id = ?", id);
        while (likeRow.next()) {
            usersLikesId.add(likeRow.getLong(1));
        }
        return usersLikesId;
    }

}
