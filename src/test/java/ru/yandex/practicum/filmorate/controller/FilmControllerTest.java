package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class FilmControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired FilmController filmController;


    @SneakyThrows
    @Test
    void testCreateFilm() {
        Film film = new Film("Alien", "A science-fiction horror", LocalDate.of(1979, 05,25), 116);
        mockMvc.perform(post("/films")
                .content(objectMapper.writeValueAsString(film))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpectAll(status().isOk(),
                        (result -> content().contentType(MediaType.APPLICATION_JSON)));
    }

    @SneakyThrows
    @Test
    void testFailName() {
        Film film = new Film("", "A science-fiction horror", LocalDate.of(1979, 05,25), 116);
        mockMvc.perform(post("/films")
                .content(objectMapper.writeValueAsString(film))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }

    @SneakyThrows
    @Test
    void testFailDescription() {
        Film film = new Film("Alien", "Based on a story by O'Bannon and Ronald Shusett, " +
                "it follows the crew of the commercial space tug Nostromo, who, " +
                "after coming across a mysterious derelict spaceship on an undiscovered moon, " +
                "find themselves up against an aggressive and deadly extraterrestrial set " +
                "loose on the Nostromo.", LocalDate.of(1979, 05,25), 116);
        mockMvc.perform(post("/films")
                        .content(objectMapper.writeValueAsString(film))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }

    @SneakyThrows
    @Test
    void testFailReleaseDate() {
        Film film = new Film("Alien", "A science-fiction horror", LocalDate.of(1895, 05,25), 116);
        mockMvc.perform(post("/films")
                        .content(objectMapper.writeValueAsString(film))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }

    @SneakyThrows
    @Test
    void testFailDuration() {
        Film film = new Film("Alien", "A science-fiction horror", LocalDate.of(1979, 05,25), -116);
        mockMvc.perform(post("/films")
                        .content(objectMapper.writeValueAsString(film))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }

    @SneakyThrows
    @Test
    void testUpdate() {
        int id = createFilm().getId();
        Film filmUpdate = new Film(id, "Alien", "update description", LocalDate.of(1979, 05,25), 116);
        mockMvc.perform(put("/films", id)
                        .content(objectMapper.writeValueAsString(filmUpdate))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(result -> content().contentType(MediaType.APPLICATION_JSON));
    }

    @SneakyThrows
    @Test
    void testUpdateUnknown() {
        int id = createFilm().getId();
        Film filmUpdate = new Film(15, "Alien", "update description", LocalDate.of(1979, 05,25), 116);
        mockMvc.perform(put("/films", id)
                        .content(objectMapper.writeValueAsString(filmUpdate))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }

    @SneakyThrows
    @Test
    void testGetAll() {
        createFilm().getId();
        mockMvc.perform(get("/films"))
                .andDo(print())
                .andExpectAll(status().isOk()
                        , (content().contentType(MediaType.APPLICATION_JSON)));
    }

    private Film createFilm() {
        Film film = new Film("Alien", "A science-fiction horror", LocalDate.of(1979, 05,25), 116);
        return filmController.storage.addNewFilm(film);
    }

}