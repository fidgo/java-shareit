package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.bind.MethodArgumentNotValidException;
import ru.practicum.shareit.user.interfaces.UserService;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc
public class UserControllerTest {

    @MockBean
    private UserService userService;

    @Autowired
    MockMvc mockMvc;

    static ObjectMapper objectMapper;

    @BeforeAll
    static void beforeAll() {
        objectMapper = new ObjectMapper();
    }

    @Test
    void getAll() throws Exception {
        User user1 = new User(1L, "user1", "user1@mail.ru");
        UserDto user1Dto = new UserDto(1L, "user1", "user1@mail.ru");
        when(userService.getAll()).thenReturn(Collections.singletonList(user1Dto));

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(content().json("[{\"id\":1,\"name\":\"user1\",\"email\":\"user1@mail.ru\"}]"));

        verify(userService, times(1)).getAll();
    }

    @Test
    void getUser() throws Exception {
        UserDto user1Dto = new UserDto(1L, "user1", "user1@mail.ru");
        when(userService.get(1L)).thenReturn(user1Dto);

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"id\":1,\"name\":\"user1\",\"email\":\"user1@mail.ru\"}"));

        verify(userService, times(1)).get(1L);
    }

    @Test
    void create() throws Exception {
        UserDto user1Dto = new UserDto(1L, "user1", "user1@mail.ru");
        when(userService.create(any(UserDto.class))).thenReturn(user1Dto);
        Map<String, String> body = new HashMap<>();
        body.put("id", "1");
        body.put("name", "user1");
        body.put("email", "user1@mail.ru");

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"id\":1,\"name\":\"user1\",\"email\":\"user1@mail.ru\"}"));

        verify(userService, times(1)).create(any(UserDto.class));
    }

    @Test
    void createWrongEmailUserExepValid() throws Exception {
        UserDto user1Dto = new UserDto(1L, "user1", "user1@mail.ru");
        Map<String, String> body = new HashMap<>();
        body.put("id", "1");
        body.put("name", "user1");
        body.put("email", "use");

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isBadRequest())
                .andExpect(result ->
                        assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException));
    }

    @Test
    void update() throws Exception {
        UserDto user1Dto = new UserDto(1L, "user1", "user1@mail.ru");
        when(userService.update(any(UserDto.class), eq(1L))).thenReturn(user1Dto);
        Map<String, String> body = new HashMap<>();
        body.put("id", "1");
        body.put("name", "user1");
        body.put("email", "user1@mail.ru");

        mockMvc.perform(patch("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"id\":1,\"name\":\"user1\",\"email\":\"user1@mail.ru\"}"));

        verify(userService, times(1)).update(any(UserDto.class), eq(1L));
    }

    @Test
    void delete() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/users/1"))
                .andExpect(status().isOk());

        verify(userService, times(1)).delete(1L);
    }
}
