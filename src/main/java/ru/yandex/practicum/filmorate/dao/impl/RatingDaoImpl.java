package ru.yandex.practicum.filmorate.dao.impl;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.RatingDao;
import ru.yandex.practicum.filmorate.exceptions.NoSuchMPAException;
import ru.yandex.practicum.filmorate.model.Rating;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
public class RatingDaoImpl implements RatingDao {
    private final JdbcTemplate jdbcTemplate;

    public RatingDaoImpl(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate=jdbcTemplate;
    }

    @Override
    public List<Rating> getAllRating() {
        return jdbcTemplate.query("select * from rating", (rs, rowNum) -> makeRating(rs));
    }

    @Override
    public Rating getRatingById(long id) {
        return jdbcTemplate.query("select * from rating where rating_id = ?", (rs, rowNum) -> makeRating(rs), id)
                .stream().findFirst().orElseThrow(() -> {
                    throw new NoSuchMPAException("No such rating");
                });
    }

    private Rating makeRating(ResultSet rs) throws SQLException {
        return Rating.builder()
                .id(rs.getInt("rating_id"))
                .name(rs.getString("name"))
                .build();
    }
}
