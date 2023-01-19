package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;


import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired UserController userController;

    @SneakyThrows
    @Test
    void testCreateUser() {
        User user = new User("ivanivanov@yandex.ru", "ivan1234", "ivan", LocalDate.of(1980, 05, 15));
        mockMvc.perform(post("/users")
                .content(objectMapper.writeValueAsString(user))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpectAll(status().isOk(),
                        (result -> content().contentType(MediaType.APPLICATION_JSON)));
    }

    @SneakyThrows
    @Test
    void testFailEmail() {
        User user = new User("ivanivanovyandex.ru", "ivan1234", "ivan", LocalDate.of(1980, 05, 15));
        mockMvc.perform(post("/users")
                .content(objectMapper.writeValueAsString(user))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }

    @SneakyThrows
    @Test
    void testFailLogin() {
        User user = new User("ivanivanov@yandex.ru", "ivan 1234", "ivan", LocalDate.of(1980, 05, 15));
        mockMvc.perform(post("/users")
                .content(objectMapper.writeValueAsString(user))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }

    @SneakyThrows
    @Test
    void testFailBirthday() {
        User user = new User("ivanivanov@yandex.ru", "ivan1234", "ivan", LocalDate.of(2050, 05, 15));
        mockMvc.perform(post("/users")
                .content(objectMapper.writeValueAsString(user))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }

    @SneakyThrows
    @Test
    void testUpdate() {
        int id = createUser().getId();
        User userUpdate = new User(id,"ivanivanov@yandex.ru", "ivan1234", "ivanUpdate", LocalDate.of(1980, 05, 15));
        mockMvc.perform(put("/users", id)
                        .content(objectMapper.writeValueAsString(userUpdate))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(result -> content().contentType(MediaType.APPLICATION_JSON));
    }

    @SneakyThrows
    @Test
    void testUpdateUnknown() {
        int id = createUser().getId();
        User userUpdate = new User(10,"ivanivanov@yandex.ru", "ivan1234", "ivanUpdate", LocalDate.of(1980, 05, 15));
        mockMvc.perform(put("/users", id)
                        .content(objectMapper.writeValueAsString(userUpdate))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }

    @SneakyThrows
    @Test
    void testGetAll() {
        createUser().getId();
        mockMvc.perform(get("/users"))
                .andDo(print())
                .andExpectAll(status().isOk()
                        , (content().contentType(MediaType.APPLICATION_JSON)));
    }

    @Test
    void testWithEmptyName() throws Exception {
        User user = new User("ivanivanov@yandex.ru", "ivan1234", LocalDate.of(1980, 05, 15));
        mockMvc.perform(post("/users")
                .content(objectMapper.writeValueAsString(user))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(result -> content().contentType(MediaType.APPLICATION_JSON));
    }

     private User createUser() {
        InMemoryUserStorage inMemoryUserStorage = new InMemoryUserStorage();
        User user = new User("ivanivanov@yandex.ru", "ivan1234", "ivan", LocalDate.of(1980, 05, 15));
        return inMemoryUserStorage.addNewUser(user);
    }
}