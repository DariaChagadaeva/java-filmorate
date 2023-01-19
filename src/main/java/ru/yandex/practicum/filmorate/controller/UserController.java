package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.exceptions.NoSuchUserException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    @Autowired
    private InMemoryUserStorage storage;

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        try {
            validateUser(user);
            log.info("Creating user {}", user);
            return storage.addNewUser(user);
        } catch (ValidationException e) {
            log.debug(e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PutMapping
    public User update(@Valid @RequestBody User user) {
        try {
            log.info("Updating user {}", user);
            return storage.updateUser(user);
        } catch (NoSuchUserException e) {
            log.debug("The user does not exist");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The user does not exist");
        } catch (ValidationException e) {
            log.debug(e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @GetMapping
    public List<User> getAll() {
        final List<User> users = storage.getUsers();
        log.info("Get all users {}", users.size());
        return users;
    }

    void validateUser(User user) {
        if(user.getLogin().contains(" ")) {
            throw new ValidationException("User login invalid");
        }
        if(user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException("User birthday invalid");
        }
    }
}
