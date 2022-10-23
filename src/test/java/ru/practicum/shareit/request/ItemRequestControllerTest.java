package ru.practicum.shareit.request;

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
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponsesDto;
import ru.practicum.shareit.request.interfaces.ItemRequestService;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemRequestController.class)
@AutoConfigureMockMvc
public class ItemRequestControllerTest {
    @MockBean
    private ItemRequestService itemRequestService;

    @Autowired
    private MockMvc mockMvc;

    private static ItemRequestResponsesDto itemReqRepDto;

    private static LocalDateTime time1;

    private static ObjectMapper objectMapper;

    @BeforeAll
    static void beforeAll() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        time1 = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);

        itemReqRepDto = new ItemRequestResponsesDto(1L, "need car", time1, new ArrayList<>());
    }

    @Test
    void getOwnRequests() throws Exception {
        when(itemRequestService.getOwn(anyLong())).thenReturn(List.of(itemReqRepDto));

        mockMvc.perform(MockMvcRequestBuilders.get("/requests").header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(content().json("[{\"id\":1,\"description\":\"need car\",\"created\":" +
                        "\"" + time1 + "\",\"items\":[]}]"));

        verify(itemRequestService, times(1))
                .getOwn(eq(1L));
    }

    @Test
    void getRequest() throws Exception {
        when(itemRequestService.getById(anyLong(), anyLong())).thenReturn(itemReqRepDto);

        mockMvc.perform(MockMvcRequestBuilders.get("/requests/{requestId}", "1")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"id\":1,\"description\":\"need car\",\"created\":" +
                        "\"" + time1 + "\",\"items\":[]}"));

        verify(itemRequestService, times(1))
                .getById(eq(1L), eq(1L));
    }

    @Test
    void getAllRequests() throws Exception {
        when(itemRequestService.getAll(anyLong(), any())).thenReturn(List.of(itemReqRepDto));

        mockMvc.perform(MockMvcRequestBuilders.get("/requests/all")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(content().json("[{\"id\":1,\"description\":\"need car\",\"created\":" +
                        "\"" + time1 + "\",\"items\":[]}]"));

        int size = 10;
        int from = 0;
        PageRequest pageRequest = new PageRequestFrom(size, from, Sort.by("created").descending());
        verify(itemRequestService, times(1))
                .getAll(eq(1L), eq(pageRequest));
    }

    @Test
    void create() throws Exception {
        ItemRequestDto itemRequestDto = new ItemRequestDto(1L, "need car", time1);
        when(itemRequestService.create(anyLong(), any())).thenReturn(itemRequestDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/requests")
                        .header("X-Sharer-User-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemRequestDto)))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"id\":1,\"description\":\"need car\",\"created\":" +
                        "\"" + time1 + "\"}"));

        verify(itemRequestService, times(1))
                .create(eq(1L), any());
    }

}
