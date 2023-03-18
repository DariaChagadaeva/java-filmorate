package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.dao.FilmStorage;
import ru.yandex.practicum.filmorate.dao.GenreDao;
import ru.yandex.practicum.filmorate.dao.RatingDao;
import ru.yandex.practicum.filmorate.dao.UserStorage;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Sql({"/test.sql"})
public class DbTests {
    private final UserStorage userStorage;
    private final FilmStorage filmStorage;
    private final GenreDao genreStorage;
    private final RatingDao ratingStorage;

    @Test
    void testAddUser() {
        userStorage.addNewUser(User.builder()
                .name("NewUser")
                .login("NewUser")
                .email("newuser1@yandex.ru")
                .birthday(LocalDate.of(1990, 01, 01))
                .build());
        Assertions.assertEquals(4, userStorage.getUsers().size());
    }

    @Test
    void testAddFilm() {
        filmStorage.addNewFilm(
                Film.builder()
                        .name("NewFilm")
                        .description("NewFilm description")
                        .releaseDate(LocalDate.of(1985,01,01))
                        .duration(120)
                        .mpa(Rating.builder().id(1).build())
                        .build());
        Assertions.assertEquals(4, filmStorage.getFilms().size());
    }

    @Test
    void testGetUserById() {
        Assertions.assertEquals(1, userStorage.findUserById(userStorage.getUsers()
                .stream().filter(user -> user.getName().equals("User1")).findAny().get().getId()).getId());
    }

    @Test
    void testGetFilmById() {
        //Assertions.assertEquals(2, filmService.findFilmById(2).getId());
        Assertions.assertEquals(2, filmStorage.findFilmById(filmStorage.getFilms()
                .stream().filter(film -> film.getName().equals("Film2")).findAny().get().getId()).getId());
    }

    @Test
    void testGetUsers() {
        Assertions.assertEquals(3, userStorage.getUsers().size());
    }

    @Test
    void testGetFilms() {
        Assertions.assertEquals(3, filmStorage.getFilms().size());
    }

    @Test
    void getMostPopularFilms() {
        Assertions.assertEquals(3, filmStorage.getMostPopularFilms(10).size());
    }

    @Test
    void testUpdateUser() {
        User user = userStorage.updateUser(User.builder()
                .id(userStorage.findUserById(userStorage.getUsers()
                                .stream().filter(u -> u.getName().equals("User1")).findAny().get().getId()).getId())
                .name("NewUser")
                .login("NewUser")
                .email("newuser1@yandex.ru")
                .birthday(LocalDate.of(1990, 01, 01))
                .build());
        Assertions.assertEquals(1, userStorage.findUserById(user.getId()).getId());
        Assertions.assertEquals("NewUser", userStorage.findUserById(user.getId()).getName());
        Assertions.assertEquals("newuser1@yandex.ru", userStorage.findUserById(user.getId()).getEmail());
    }

    @Test
    void testUpdateFilm() {
            Film film = filmStorage.updateFilm(Film.builder()
                .id(filmStorage.findFilmById(filmStorage.getFilms()
                        .stream().filter(f -> f.getName().equals("Film1")).findAny().get().getId()).getId())
                .name("NewFilm")
                .description("NewFilm description")
                .releaseDate(LocalDate.of(1985,01,01))
                .duration(120)
                .mpa(Rating.builder().id(3).build())
                .build());
            Assertions.assertEquals("NewFilm", filmStorage.findFilmById(film.getId()).getName());
            Assertions.assertEquals("NewFilm description", filmStorage.findFilmById(film.getId()).getDescription());
            Assertions.assertEquals(3, filmStorage.findFilmById(film.getId()).getMpa().getId());

    }

    @Test
    void testGetGenreById() {
        Assertions.assertEquals("Комедия", genreStorage.getGenreById(1).getName());
        Assertions.assertEquals("Драма", genreStorage.getGenreById(2).getName());
        Assertions.assertEquals("Мультфильм", genreStorage.getGenreById(3).getName());
        Assertions.assertEquals("Триллер", genreStorage.getGenreById(4).getName());
        Assertions.assertEquals("Документальный", genreStorage.getGenreById(5).getName());
        Assertions.assertEquals("Боевик", genreStorage.getGenreById(6).getName());
    }

    @Test
    void testGetRatingById() {
        Assertions.assertEquals("G", ratingStorage.getRatingById(1).getName());
        Assertions.assertEquals("PG", ratingStorage.getRatingById(2).getName());
        Assertions.assertEquals("PG-13", ratingStorage.getRatingById(3).getName());
        Assertions.assertEquals("R", ratingStorage.getRatingById(4).getName());
        Assertions.assertEquals("NC-17", ratingStorage.getRatingById(5).getName());
    }

    @Test
    void getAllGenres() {
        Assertions.assertEquals(6, genreStorage.getAllGenres().size());
    }

    @Test
    void getAllRating() {
        Assertions.assertEquals(5, ratingStorage.getAllRating().size());
    }

    @Test
    void addLike() {
        filmStorage.addLike(2, 1);
        filmStorage.addLike(2, 3);
        Assertions.assertEquals(2, filmStorage.findFilmById(2).getLikes().size());
    }

    @Test
    void deleteLike() {
        filmStorage.addLike(2, 1);
        filmStorage.addLike(2, 3);
        filmStorage.deleteLike(2,1);
        Assertions.assertEquals(1, filmStorage.findFilmById(2).getLikes().size());
    }

    @Test
    void addToFriend() {
        userStorage.addFriend(1,2);
        userStorage.addFriend(1,3);
        userStorage.addFriend(2,3);
        Assertions.assertEquals(2, userStorage.getFriendsList(1).size());
        Assertions.assertEquals(1, userStorage.getFriendsList(2).size());
        Assertions.assertEquals(0, userStorage.getFriendsList(3).size());
    }

    @Test
    void getUserFriends() {
        userStorage.addFriend(1,2);
        userStorage.addFriend(1,3);
        Assertions.assertEquals(2, userStorage.getFriendsList(1).size());

    }

}
