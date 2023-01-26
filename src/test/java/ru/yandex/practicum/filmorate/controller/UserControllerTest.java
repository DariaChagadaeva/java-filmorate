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
        User user = User.builder()
                .email("ivanivanov@yandex.ru").login("ivan1234").name("ivan")
                .birthday(LocalDate.of(1980, 05, 15)).build();
        mockMvc.perform(post("/users")
                .content(objectMapper.writeValueAsString(user))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpectAll(status().isOk(),
                        (result -> content().contentType(MediaType.APPLICATION_JSON)));
    }

    @SneakyThrows
    @Test
    void testFailName() {
        User user = User.builder()
                .email("ivanivanov@yandex.ru").login("ivan1234").name("")
                .birthday(LocalDate.of(1980, 05, 15)).build();
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
        User user = User.builder()
                .email("ivanivanovyandex.ru").login("ivan1234").name("ivan")
                .birthday(LocalDate.of(1980, 05, 15)).build();
        mockMvc.perform(post("/users")
                .content(objectMapper.writeValueAsString(user))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }

    @SneakyThrows
    @Test
    void testFailLogin() {
        User user = User.builder()
                .email("ivanivanov@yandex.ru").login("ivan 1234").name("ivan")
                .birthday(LocalDate.of(1980, 05, 15)).build();
        mockMvc.perform(post("/users")
                .content(objectMapper.writeValueAsString(user))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }

    @SneakyThrows
    @Test
    void testFailBirthday() {
        User user = User.builder()
                .email("ivanivanov@yandex.ru").login("ivan1234").name("ivan")
                .birthday(LocalDate.of(2050, 05, 15)).build();
        mockMvc.perform(post("/users")
                .content(objectMapper.writeValueAsString(user))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }

    @SneakyThrows
    @Test
    void testUpdate() {
        long id = createUser().getId();
        User userUpdate = User.builder().id(id)
                .email("ivanivanov@yandex.ru").login("ivan1234").name("ivanUpdate")
                .birthday(LocalDate.of(1980, 05, 15)).build();
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
        long id = createUser().getId();
        User userUpdate = User.builder().id(10)
                .email("ivanivanov@yandex.ru").login("ivan1234").name("ivanUpdate")
                .birthday(LocalDate.of(1980, 05, 15)).build();
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
        User user = User.builder()
                .email("ivanivanov@yandex.ru").login("ivan1234")
                .birthday(LocalDate.of(1980, 05, 15)).build();
        mockMvc.perform(post("/users")
                .content(objectMapper.writeValueAsString(user))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(result -> content().contentType(MediaType.APPLICATION_JSON));
    }

     private User createUser() {
        InMemoryUserStorage inMemoryUserStorage = new InMemoryUserStorage();
        User user = User.builder()
                .email("ivanivanov@yandex.ru").login("ivan1234").name("ivan")
                .birthday(LocalDate.of(1980, 05, 15)).build();
        return inMemoryUserStorage.addNewUser(user);
    }
}