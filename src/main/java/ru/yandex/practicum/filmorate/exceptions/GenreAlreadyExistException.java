package ru.yandex.practicum.filmorate.exceptions;

public class GenreAlreadyExistException extends RuntimeException{

    public GenreAlreadyExistException(String message) {
        super(message);
    }
}
