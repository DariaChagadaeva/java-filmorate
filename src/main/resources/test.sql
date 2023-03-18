DELETE FROM FRIENDSHIP;
ALTER TABLE FRIENDSHIP ALTER COLUMN friendship_id RESTART WITH 1;
DELETE FROM FILM_LIKES;
DELETE FROM USERS;
ALTER TABLE USERS ALTER COLUMN user_id RESTART WITH 1;
DELETE FROM FILM_GENRE;
DELETE FROM GENRE;
ALTER TABLE GENRE ALTER COLUMN genre_id RESTART WITH 1;
DELETE FROM FILMS;
ALTER TABLE FILMS ALTER COLUMN film_id RESTART WITH 1;
DELETE FROM RATING;
ALTER TABLE RATING ALTER COLUMN rating_id RESTART WITH 1;

INSERT INTO RATING (name) values('G'), ('PG'), ('PG-13'), ('R'), ('NC-17');
INSERT INTO GENRE (name) values('Комедия'), ('Драма'), ('Мультфильм'), ('Триллер'), ('Документальный'), ('Боевик');

INSERT INTO USERS (name, login, email, birthday) values('User1', 'User1', 'user1@yandex.ru', '1980-01-01');
INSERT INTO USERS (name, login, email, birthday) values('User2', 'User2', 'user2@yandex.ru', '1985-02-02');
INSERT INTO USERS (name, login, email, birthday) values('User3', 'User3', 'user3@yandex.ru', '1990-03-03');

INSERT INTO FILMS (name, description, release_date, duration, rating_id) values('Film1', 'Film1 Description', '1980-10-10', '120', '2');
INSERT INTO FILMS (name, description, release_date, duration, rating_id) values('Film2', 'Film2 Description', '1970-05-05', '150', '4');
INSERT INTO FILMS (name, description, release_date, duration, rating_id) values('Film3', 'Film3 Description', '2000-10-10', '130', '3');
INSERT INTO FILM_GENRE (film_id, genre_id) values(1, 1);