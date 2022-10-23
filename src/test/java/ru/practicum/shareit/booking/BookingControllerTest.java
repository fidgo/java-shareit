package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
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
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingUpdateDto;
import ru.practicum.shareit.booking.interfaces.BookingService;
import ru.practicum.shareit.error.InvalidArgumentException;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
@AutoConfigureMockMvc
public class BookingControllerTest {
    @MockBean
    private BookingService bookingService;

    @Autowired
    private MockMvc mockMvc;

    private static ObjectMapper objectMapper;

    private static LocalDateTime start;
    private static LocalDateTime end;

    private static BookingUpdateDto bookingUpdateDto;


    @BeforeAll
    static void beforeAll() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        start = LocalDateTime.of(2022, 10, 15, 10, 30);
        end = LocalDateTime.of(2022, 10, 16, 11, 30);
        bookingUpdateDto = new BookingUpdateDto(1L,
                BookingState.WAITING,
                new BookingUpdateDto.UserBookingUpdateDto(1L, "user1", "user1@mail.ru"),
                new BookingUpdateDto.ItemBookingUpdateDto(1L, "car", "very fast", true),
                start,
                end);

    }

    @Test
    void getById() throws Exception {
        when(bookingService.getById(anyLong(), anyLong())).thenReturn(bookingUpdateDto);

        mockMvc.perform(MockMvcRequestBuilders.get("/bookings/1").header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"id\":1,\"status\":\"WAITING\",\"booker\":{\"id\":1,\"name\"" +
                        ":\"user1\",\"email\":\"user1@mail.ru\"},\"item\":{\"id\":1,\"name\":\"car\",\"description\"" +
                        ":\"very fast\",\"available\":true},\"start\":\"2022-10-15T10:30:00\"," +
                        "\"end\":\"2022-10-16T11:30:00\"}"));

        verify(bookingService, times(1))
                .getById(1L, 1L);
    }

    @Test
    void getAllByBookerId() throws Exception {
        int size = 10;
        int from = 0;
        PageRequest pageRequest = new PageRequestFrom(size, from, Sort.by("start").descending());

        when(bookingService.getAllByBookerId(anyLong(), any(), any()))
                .thenReturn(Collections.singletonList(bookingUpdateDto));

        mockMvc.perform(MockMvcRequestBuilders.get("/bookings").header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(content().json("[{\"id\":1,\"status\":\"WAITING\",\"booker\":{\"id\":1,\"name\"" +
                        ":\"user1\",\"email\":\"user1@mail.ru\"},\"item\":{\"id\":1,\"name\":\"car\",\"description\"" +
                        ":\"very fast\",\"available\":true},\"start\":\"2022-10-15T10:30:00\"," +
                        "\"end\":\"2022-10-16T11:30:00\"}]"));

        verify(bookingService, times(1))
                .getAllByBookerId(eq(1L), any(), eq(pageRequest));
    }

    @Test
    void getAllByBookerIdIllegalArgumentEx() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/bookings").header("X-Sharer-User-Id", "1")
                        .param("state", "invalid_state"))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof InvalidArgumentException))
                .andExpect(result -> assertEquals("Unknown state: UNSUPPORTED_STATUS",
                        result.getResolvedException().getMessage()));
    }


    @Test
    void getAllByOwnerId() throws Exception {
        int size = 10;
        int from = 0;
        PageRequest pageRequest = new PageRequestFrom(size, from, Sort.by("start").descending());

        when(bookingService.getAllByOwnerId(anyLong(), any(), any()))
                .thenReturn(Collections.singletonList(bookingUpdateDto));

        mockMvc.perform(MockMvcRequestBuilders.get("/bookings/owner").header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(content().json("[{\"id\":1,\"status\":\"WAITING\",\"booker\":{\"id\":1,\"name\"" +
                        ":\"user1\",\"email\":\"user1@mail.ru\"},\"item\":{\"id\":1,\"name\":\"car\",\"description\"" +
                        ":\"very fast\",\"available\":true},\"start\":\"2022-10-15T10:30:00\"," +
                        "\"end\":\"2022-10-16T11:30:00\"}]"));

        verify(bookingService, times(1))
                .getAllByOwnerId(eq(1L), any(), eq(pageRequest));
    }

    @Test
    void create() throws Exception {
        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        ;
        LocalDateTime start = now.plusDays(1);
        LocalDateTime end = now.plusDays(2);
        BookingDto bookingDto = new BookingDto(1L,
                1L,
                start,
                end,
                BookingState.WAITING,
                new BookingDto.UserBookingDto(1L, "user1", "user1@mail.ru"),
                new BookingDto.ItemBookingDto(1L, "car", "very fast", true));

        when(bookingService.create(anyLong(), any())).thenReturn(bookingDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingDto))
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"id\":1,\"itemId\":1,\"start\":" + "\"" + start +
                        "\",\"end\":\"" + end + "\",\"status\":\"WAITING\",\"booker\":{\"id\":1,\"name\":\"user1\"" +
                        ",\"email\":\"user1@mail.ru\"},\"item\":{\"id\":1,\"name\":\"car\",\"description\"" +
                        ":\"very fast\",\"available\":true}}"));

        verify(bookingService, times(1))
                .create(eq(1L), any(BookingDto.class));
    }

    @Test
    void updateState() throws Exception {
        when(bookingService.updateState(anyLong(), anyLong(), anyBoolean())).thenReturn(bookingUpdateDto);

        mockMvc.perform(MockMvcRequestBuilders.patch("/bookings/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", "1")
                        .param("approved", "true"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"id\":1,\"status\":\"WAITING\",\"booker\":{\"id\":1,\"name\"" +
                        ":\"user1\",\"email\":\"user1@mail.ru\"},\"item\":{\"id\":1,\"name\":\"car\",\"description\"" +
                        ":\"very fast\",\"available\":true},\"start\":\"2022-10-15T10:30:00\"," +
                        "\"end\":\"2022-10-16T11:30:00\"}"));

        verify(bookingService, times(1))
                .updateState(eq(1L), eq(1L), anyBoolean());
    }

}
