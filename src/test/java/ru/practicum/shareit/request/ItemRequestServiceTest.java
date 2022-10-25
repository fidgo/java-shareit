package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.PageRequestFrom;
import ru.practicum.shareit.item.interfaces.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponsesDto;
import ru.practicum.shareit.request.interfaces.ItemRequestRepository;
import ru.practicum.shareit.request.interfaces.ItemRequestService;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.interfaces.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

public class ItemRequestServiceTest {
    ItemRequestService itemRequestService;
    private UserRepository userRepository;
    private ItemRepository itemRepository;
    private ItemRequestRepository itemRequestRepository;

    private User user1;

    private User user2;
    private Item item1;

    private ItemRequest item1Request;

    private ItemRequestResponsesDto item1RequestResponsesDto;

    @BeforeEach
    void beforeEach() {
        userRepository = mock(UserRepository.class);
        itemRepository = mock(ItemRepository.class);
        itemRequestRepository = mock(ItemRequestRepository.class);
        itemRequestService = new ItemRequestServiceImpl(userRepository, itemRepository, itemRequestRepository);

        user1 = new User(1L, "user1", "user1@mail.ru");
        user2 = new User(2L, "user2", "user2@mail.ru");
        item1 = new Item(1L, "car", "very fast", true, user1, null);
        item1Request = new ItemRequest(1L, "need a car", user2, null);
        item1RequestResponsesDto = new ItemRequestResponsesDto(1L, "need a car", null,
                new ArrayList<>());
    }

    @Test
    void create() {
        ItemRequestDto itemRequestDto = new ItemRequestDto(1L, "need a car", null);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user2));
        when(itemRequestRepository.save(any())).thenReturn(item1Request);

        ItemRequestDto from = itemRequestService.create(2L, itemRequestDto);
        verify(userRepository, Mockito.times(1)).findById(anyLong());

        assertNotNull(from);
        assertEquals(itemRequestDto.getId(), from.getId());
        assertEquals(itemRequestDto.getDescription(), from.getDescription());
    }

    @Test
    void getOwn() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user2));
        when(itemRequestRepository.findAllByRequestor_IdOrderByCreatedDesc(anyLong())).thenReturn(List.of(item1Request));

        List<ItemRequestResponsesDto> itemRequestResponsesDtos =
                itemRequestService.getOwn(2L);

        verify(userRepository, Mockito.times(1)).findById(anyLong());
        verify(itemRequestRepository, Mockito.times(1))
                .findAllByRequestor_IdOrderByCreatedDesc(anyLong());

        assertNotNull(itemRequestResponsesDtos);
        assertEquals(1, itemRequestResponsesDtos.size());
        ItemRequestResponsesDto from = itemRequestResponsesDtos.get(0);
        assertEquals(item1RequestResponsesDto.getId(), from.getId());
        assertEquals(item1RequestResponsesDto.getDescription(), from.getDescription());
    }

    @Test
    void getById() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user2));
        when(itemRequestRepository.findById(anyLong())).thenReturn(Optional.of(item1Request));

        ItemRequestResponsesDto from = itemRequestService.getById(2L, 1L);
        verify(userRepository, Mockito.times(1)).findById(anyLong());
        verify(itemRequestRepository, Mockito.times(1)).findById(anyLong());

        assertNotNull(from);
        assertEquals(item1RequestResponsesDto.getId(), from.getId());
        assertEquals(item1RequestResponsesDto.getDescription(), from.getDescription());
    }

    @Test
    void getAll() {

        PageImpl<ItemRequest> pgItemRequest = new PageImpl<>(List.of(item1Request));
        int size = 10;
        int from = 0;
        PageRequest pageRequest = new PageRequestFrom(size, from, Sort.by("start").descending());

        when(itemRequestRepository.findAllByRequestor_IdNot(anyLong(), any())).thenReturn(pgItemRequest);

        List<ItemRequestResponsesDto> listFrom = itemRequestService.getAll(2L, pageRequest);
        verify(itemRequestRepository, Mockito.times(1))
                .findAllByRequestor_IdNot(anyLong(), any());
        assertNotNull(listFrom);
        assertEquals(1, listFrom.size());
        ItemRequestResponsesDto fromRep = listFrom.get(0);
        assertEquals(item1RequestResponsesDto.getId(), fromRep.getId());
        assertEquals(item1RequestResponsesDto.getDescription(), fromRep.getDescription());

    }

    @Test
    void itemRequestMapperItemRequestResponsesDto() {
        item1.setRequest(item1Request);
        ItemRequestResponsesDto.ItemReqResponses expResponse = new ItemRequestResponsesDto.ItemReqResponses();
        expResponse.setId(item1.getId());
        expResponse.setName(item1.getName());
        expResponse.setDescription(item1.getDescription());
        expResponse.setAvailable(item1.getAvailable());
        expResponse.setRequestId(item1.getRequest().getId());

        ItemRequestResponsesDto.ItemReqResponses response = ItemRequestMapper.toItemReqResponses(item1);
        assertNotNull(response);
        assertEquals(expResponse.getId(), response.getId());
        assertEquals(expResponse.getName(), response.getName());
        assertEquals(expResponse.getDescription(), response.getDescription());
        assertEquals(expResponse.getRequestId(), response.getRequestId());

        List<ItemRequestResponsesDto.ItemReqResponses> responses =
                ItemRequestMapper.toItemRequestResponse(List.of(item1));
        assertNotNull(responses);
        assertEquals(1, responses.size());
        response = responses.get(0);
        assertEquals(expResponse.getId(), response.getId());
        assertEquals(expResponse.getName(), response.getName());
        assertEquals(expResponse.getDescription(), response.getDescription());
        assertEquals(expResponse.getRequestId(), response.getRequestId());
    }

    @Test
    void itemRequestMapperToItemRequests() {
        ItemRequestDto expItemReqDto = ItemRequestMapper.toItemRequestDto(item1Request);
        List<ItemRequestDto> itemRequestDtos = ItemRequestMapper.toItemRequests(List.of(item1Request));
        assertNotNull(itemRequestDtos);
        assertEquals(1, itemRequestDtos.size());
        ItemRequestDto response = itemRequestDtos.get(0);

        assertEquals(response.getId(), expItemReqDto.getId());
        assertEquals(response.getCreated(), expItemReqDto.getCreated());
        assertEquals(response.getDescription(), expItemReqDto.getDescription());
    }
}
