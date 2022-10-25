package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.practicum.shareit.PageRequestFrom;
import ru.practicum.shareit.error.StatusElemException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemInfoDto;
import ru.practicum.shareit.item.interfaces.ItemService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
@AutoConfigureMockMvc
public class ItemControllerTest {

    @MockBean
    private ItemService itemService;

    @Autowired
    private MockMvc mockMvc;

    private static ObjectMapper objectMapper;

    @BeforeAll
    static void beforeAll() {
        objectMapper = new ObjectMapper();
    }

    @Test
    void getAll() throws Exception {
        ItemInfoDto dto = new ItemInfoDto(1L, "car", "very fast", true,
                new ItemInfoDto.BookingInfoDto(1L, 5L),
                new ItemInfoDto.BookingInfoDto(2L, 3L),
                null);

        when(itemService.getAllItemsByUserID(anyLong(), any())).thenReturn(Collections.singletonList(dto));

        mockMvc.perform(MockMvcRequestBuilders.get("/items").header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(content().json("[{\"id\":1,\"name\":\"car\",\"description\":\"very fast\"," +
                        "\"available\":true,\"lastBooking\":{\"id\":1,\"bookerId\":5},\"nextBooking\":{\"id\":2," +
                        "\"bookerId\":3},\"comments\":null}]"));


        PageRequest pageRequest = new PageRequestFrom(10, 0, Sort.unsorted());
        verify(itemService, times(1))
                .getAllItemsByUserID(1L, pageRequest);
    }

    @Test
    void get() throws Exception {
        ItemInfoDto dto = new ItemInfoDto(1L, "car", "very fast", true,
                new ItemInfoDto.BookingInfoDto(1L, 5L),
                new ItemInfoDto.BookingInfoDto(2L, 3L),
                null);

        when(itemService.get(anyLong(), anyLong())).thenReturn(dto);

        mockMvc.perform(MockMvcRequestBuilders.get("/items/1").header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"id\":1,\"name\":\"car\",\"description\":\"very fast\"," +
                        "\"available\":true,\"lastBooking\":{\"id\":1,\"bookerId\":5},\"nextBooking\":{\"id\":2," +
                        "\"bookerId\":3},\"comments\":null}"));

        verify(itemService, times(1))
                .get(1L, 1L);
    }

    @Test
    void search() throws Exception {
        ItemDto dto = new ItemDto(1L, "car", "very Fast", true, 3L);

        when(itemService.search(anyString(), any())).thenReturn(Collections.singletonList(dto));

        mockMvc.perform(MockMvcRequestBuilders.get("/items/search")
                        .param("text", "car")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(content().json("[{\"id\":1,\"name\":\"car\",\"description\":\"very Fast\"," +
                        "\"available\":true,\"requestId\":3}]"));

        PageRequest pageRequest = new PageRequestFrom(10, 0, Sort.unsorted());
        verify(itemService, times(1))
                .search("car", pageRequest);
    }

    @Test
    void create() throws Exception {
        ItemDto dto = new ItemDto(1L, "car", "very Fast", true, 3L);

        when(itemService.create(anyLong(), any())).thenReturn(dto);

        Map<String, String> body = new HashMap<>();
        body.put("id", "1");
        body.put("name", "car");
        body.put("description", "very Fast");
        body.put("available", "true");
        body.put("requestId", "3");

        mockMvc.perform(MockMvcRequestBuilders.post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body))
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"id\":1,\"name\":\"car\",\"description\":\"very Fast\"" +
                        ",\"available\":true,\"requestId\":3}"));

        verify(itemService, times(1))
                .create(eq(1L), any(ItemDto.class));
    }

    @Test
    void createComment() throws Exception {
        LocalDateTime time1 = LocalDateTime.of(2022, 10, 12, 15, 16);
        CommentDto comDto = new CommentDto(1L, "nice thing!", "clark", time1);
        Long userId = 1L;
        Long itemId = 1L;

        when(itemService.createComment(anyLong(), anyLong(), any())).thenReturn(comDto);

        Map<String, String> body = new HashMap<>();
        body.put("id", "1");
        body.put("text", "nice thing!");
        body.put("authorName", "clark");
        body.put("available", "true");
        body.put("created", time1.toString());

        mockMvc.perform(MockMvcRequestBuilders.post("/items/1/comment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body))
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"id\":1,\"text\":\"nice thing!\",\"authorName\":\"clark\"" +
                        ",\"created\":\"2022-10-12T15:16:00\"}"));

        verify(itemService, times(1))
                .createComment(anyLong(), anyLong(), any(CommentDto.class));
    }

    @Test
    void createCommentStatusElemEx() throws Exception {
        LocalDateTime time1 = LocalDateTime.of(2022, 10, 12, 15, 16);

        when(itemService.createComment(anyLong(), anyLong(), any())).thenThrow(new StatusElemException("error"));

        Map<String, String> body = new HashMap<>();
        body.put("id", "1");
        body.put("text", "nice thing!");
        body.put("authorName", "clark");
        body.put("available", "true");
        body.put("created", time1.toString());

        mockMvc.perform(MockMvcRequestBuilders.post("/items/1/comment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body))
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isBadRequest())
                .andExpect(result -> {
                    assertTrue(result.getResolvedException() instanceof StatusElemException);
                })
                .andExpect(result -> {
                    assertEquals("error", result.getResolvedException().getMessage());
                });


        verify(itemService, times(1))
                .createComment(anyLong(), anyLong(), any(CommentDto.class));
    }

    @Test
    void update() throws Exception {
        ItemDto dto = new ItemDto(1L, "car", "very Fast", true, 3L);

        when(itemService.update(anyLong(), anyLong(), any())).thenReturn(dto);

        Map<String, String> body = new HashMap<>();
        body.put("id", "1");
        body.put("name", "car");
        body.put("description", "very Fast");
        body.put("available", "true");
        body.put("requestId", "3");

        mockMvc.perform(MockMvcRequestBuilders.patch("/items/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body))
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"id\":1,\"name\":\"car\",\"description\":\"very Fast\"" +
                        ",\"available\":true,\"requestId\":3}"));

        verify(itemService, times(1))
                .update(anyLong(), anyLong(), any(ItemDto.class));
    }
}
