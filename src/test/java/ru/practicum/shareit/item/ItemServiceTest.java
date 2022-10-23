package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.PageRequestFrom;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingState;
import ru.practicum.shareit.booking.interfaces.BookingRepository;
import ru.practicum.shareit.error.InvalidAccessException;
import ru.practicum.shareit.error.NoSuchElemException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemInfoDto;
import ru.practicum.shareit.item.interfaces.CommentRepository;
import ru.practicum.shareit.item.interfaces.ItemRepository;
import ru.practicum.shareit.item.interfaces.ItemService;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.interfaces.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

public class ItemServiceTest {
    private ItemService itemService;
    private UserRepository userRepository;
    private ItemRepository itemRepository;
    private BookingRepository bookingRepository;
    private CommentRepository commentRepository;

    private User user1;
    private Item item1;
    private ItemDto item1Dto;
    private ItemInfoDto item1InfoDto;
    private Booking booking1;

    private Comment comment1;

    @BeforeEach
    void beforeEach() {
        userRepository = mock(UserRepository.class);
        itemRepository = mock(ItemRepository.class);
        bookingRepository = mock(BookingRepository.class);
        commentRepository = mock(CommentRepository.class);
        itemService = new ItemServiceImpl(userRepository, itemRepository, bookingRepository, commentRepository);

        user1 = new User(1L, "user1", "user1@mail.ru");
        item1 = new Item(1L, "car", "very fast", true, user1, null);
        item1Dto = new ItemDto(1L, "car", "very fast", true, null);
        item1InfoDto = new ItemInfoDto(1L, "car", "very fast", true,
                null, null, new ArrayList<>());
        booking1 = new Booking(1L, null, null, item1, user1, BookingState.WAITING);
        comment1 = new Comment(1L, "nice thing!", user1, item1, null);
    }

    @Test
    void create() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user1));
        when(itemRepository.save(any())).thenReturn(item1);

        ItemDto itemDto = itemService.create(1L, item1Dto);
        assertNotNull(itemDto);
        verify(userRepository, Mockito.times(1)).findById(anyLong());
        verify(itemRepository, Mockito.times(1)).save(any(Item.class));

        Item itemFrom = ItemMapper.toItem(user1, itemDto, null);
        assertEquals(itemFrom.getId(), item1.getId());
        assertEquals(itemFrom.getName(), item1.getName());
        assertEquals(itemFrom.getDescription(), item1.getDescription());
        assertEquals(itemFrom.getAvailable(), item1.getAvailable());
        assertEquals(itemFrom.getOwner().getId(), item1.getOwner().getId());
    }

    @Test
    void createFindRequest() {
        ItemDto item2Dto = new ItemDto(3L, "ball", "round", true, 5L);
        User user2 = new User(2L, "user2", "user2@mail.ru");
        Item item2 = new Item(3L, "car", "very fast",
                true, user1, user2);

        when(userRepository.findById(eq(1L))).thenReturn(Optional.of(user1));
        when(userRepository.findById(eq(5L))).thenReturn(Optional.of(user2));
        when(itemRepository.save(any())).thenReturn(item2);

        ItemDto itemDto = itemService.create(1L, item2Dto);
        assertNotNull(itemDto);
        verify(userRepository, Mockito.times(2)).findById(anyLong());
        verify(itemRepository, Mockito.times(1)).save(any(Item.class));

        Item itemFrom = ItemMapper.toItem(user1, itemDto, user2);
        assertEquals(itemFrom.getId(), item2.getId());
        assertEquals(itemFrom.getName(), item2.getName());
        assertEquals(itemFrom.getDescription(), item2.getDescription());
        assertEquals(itemFrom.getAvailable(), item2.getAvailable());
        assertEquals(itemFrom.getOwner().getId(), item2.getOwner().getId());
        assertEquals(itemFrom.getRequest().getId(), user2.getId());
        assertEquals(itemFrom.getRequest().getName(), user2.getName());
        assertEquals(itemFrom.getRequest().getEmail(), user2.getEmail());
    }

    @Test
    void createFindRequestNoSuchEx() {
        ItemDto item2Dto = new ItemDto(3L, "ball", "round", true, 5L);
        User user2 = new User(2L, "user2", "user2@mail.ru");
        Item item2 = new Item(3L, "car", "very fast",
                true, user1, user2);

        when(userRepository.findById(eq(1L))).thenReturn(Optional.of(user1));
        when(userRepository.findById(eq(5L))).thenThrow(new NoSuchElemException("Нет такого пользователя"));//thenReturn(Optional.of(user2));
        when(itemRepository.save(any())).thenReturn(item2);

        NoSuchElemException ex = assertThrows(NoSuchElemException.class, () -> {
            itemService.create(1L, item2Dto);
        });
        assertEquals("Нет такого пользователя", ex.getMessage());
    }

    @Test
    void update() {
        item1.setDescription("ball");
        item1Dto.setDescription("ball");
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user1));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item1));
        when(itemRepository.save(any())).thenReturn(item1);

        ItemDto itemDto = itemService.update(1L, 1L, item1Dto);
        verify(userRepository, Mockito.times(1)).findById(anyLong());
        verify(itemRepository, Mockito.times(1)).findById(anyLong());
        verify(itemRepository, Mockito.times(1)).save(any(Item.class));

        Item itemFrom = ItemMapper.toItem(user1, itemDto, null);
        assertEquals(itemFrom.getId(), item1.getId());
        assertEquals(itemFrom.getName(), item1.getName());
        assertEquals(itemFrom.getDescription(), item1.getDescription());
        assertEquals(itemFrom.getAvailable(), item1.getAvailable());
        assertEquals(itemFrom.getOwner().getId(), item1.getOwner().getId());
    }

    @Test
    void updateInvalidAccessEx() {
        item1.setDescription("ball");
        item1Dto.setDescription("ball");
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user1));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item1));

        InvalidAccessException ex = assertThrows(InvalidAccessException.class,
                () -> {
                    itemService.update(4L, 1L, item1Dto);
                });
        assertEquals("Такой пользователь не может модифицировать предмет", ex.getMessage());
        verify(userRepository, Mockito.times(1)).findById(anyLong());
        verify(itemRepository, Mockito.times(1)).findById(anyLong());
    }

    @Test
    void updateNoSuchUser() {
        item1.setDescription("ball");
        item1Dto.setDescription("ball");
        when(userRepository.findById(eq(1L))).thenReturn(Optional.of(user1));

        NoSuchElemException ex = assertThrows(NoSuchElemException.class,
                () -> {
                    itemService.update(4L, 1L, item1Dto);
                });
        assertEquals("Нет такого пользователя", ex.getMessage());
        verify(userRepository, Mockito.times(1)).findById(anyLong());

    }

    @Test
    void updateNoSuchItem() {
        item1.setDescription("ball");
        item1Dto.setDescription("ball");
        when(userRepository.findById(eq(1L))).thenReturn(Optional.of(user1));
        when(itemRepository.findById(eq(1L))).thenReturn(Optional.of(item1));

        NoSuchElemException ex = assertThrows(NoSuchElemException.class,
                () -> {
                    itemService.update(1L, 4L, item1Dto);
                });
        assertEquals("Нет такого предмета", ex.getMessage());
        verify(userRepository, Mockito.times(1)).findById(anyLong());
        verify(itemRepository, Mockito.times(1)).findById(anyLong());
    }

    @Test
    void get() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user1));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item1));
        when(bookingRepository.findTop2ByItem_Owner_IdAndItem_IdOrderByStartAsc(anyLong(), anyLong()))
                .thenReturn(new ArrayList<>());
        when(commentRepository.findAllByItem_Id(anyLong())).thenReturn(new ArrayList<>());

        ItemInfoDto itemInfoDto = itemService.get(1L, 1L);
        verify(userRepository, Mockito.times(1)).findById(anyLong());
        verify(itemRepository, Mockito.times(1)).findById(anyLong());
        verify(bookingRepository, Mockito.times(1))
                .findTop2ByItem_Owner_IdAndItem_IdOrderByStartAsc(anyLong(), anyLong());
        verify(commentRepository, Mockito.times(1)).findAllByItem_Id(anyLong());

        assertEquals(itemInfoDto.getId(), item1InfoDto.getId());
        assertEquals(itemInfoDto.getName(), item1InfoDto.getName());
        assertEquals(itemInfoDto.getDescription(), item1InfoDto.getDescription());
        assertEquals(itemInfoDto.getAvailable(), item1InfoDto.getAvailable());
    }

    @Test
    void getAllItemsByUserID() {
        PageRequest pageRequest = new PageRequestFrom(10, 0, Sort.unsorted());
        Page<Item> pgItems = new PageImpl<>(Collections.singletonList(item1));

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user1));
        when(itemRepository.findAllByOwner(any(), any())).thenReturn(pgItems);

        List<ItemInfoDto> itemInfoDtos = itemService.getAllItemsByUserID(1L, pageRequest);
        verify(userRepository, Mockito.times(1)).findById(anyLong());
        verify(itemRepository, Mockito.times(1)).findAllByOwner(any(), any());
        assertNotNull(itemInfoDtos);
        assertEquals(1, itemInfoDtos.size());
        ItemInfoDto itemInfoDtoFrom = itemInfoDtos.get(0);
        assertEquals(itemInfoDtoFrom.getId(), item1InfoDto.getId());
        assertEquals(itemInfoDtoFrom.getName(), item1InfoDto.getName());
        assertEquals(itemInfoDtoFrom.getDescription(), item1InfoDto.getDescription());
        assertEquals(itemInfoDtoFrom.getAvailable(), item1InfoDto.getAvailable());
    }

    @Test
    void search() {
        PageRequest pageRequest = new PageRequestFrom(10, 0, Sort.unsorted());
        Page<Item> pgItems = new PageImpl<>(Collections.singletonList(item1));

        when(itemRepository.search(anyString(), any())).thenReturn(pgItems);

        List<ItemDto> itemDtos = itemService.search("ca", pageRequest);
        verify(itemRepository, Mockito.times(1)).search(any(), any());
        assertNotNull(itemDtos);
        assertEquals(1, itemDtos.size());
        ItemDto itemDto = itemDtos.get(0);
        assertEquals(itemDto.getId(), item1Dto.getId());
        assertEquals(itemDto.getName(), item1Dto.getName());
        assertEquals(itemDto.getDescription(), item1Dto.getDescription());
        assertEquals(itemDto.getAvailable(), item1Dto.getAvailable());
    }

    @Test
    void searchEmptyText() {
        PageRequest pageRequest = new PageRequestFrom(10, 0, Sort.unsorted());

        List<ItemDto> itemDtos = itemService.search("", pageRequest);

        assertNotNull(itemDtos);
        assertEquals(0, itemDtos.size());
    }


    @Test
    void createComment() {
        CommentDto commentDto1 = new CommentDto(1L, "nice thing!", user1.getName(), null);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user1));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item1));
        when(bookingRepository.findFirstByBooker_IdAndItem_IdAndEndBefore(anyLong(), anyLong(), any()))
                .thenReturn(Optional.of(booking1));
        when(commentRepository.save(any())).thenReturn(comment1);

        CommentDto dtoFrom = itemService.createComment(1L, 1L, commentDto1);
        verify(userRepository, Mockito.times(1)).findById(anyLong());
        verify(itemRepository, Mockito.times(1)).findById(anyLong());
        verify(bookingRepository, Mockito.times(1))
                .findFirstByBooker_IdAndItem_IdAndEndBefore(anyLong(), anyLong(), any());
        verify(commentRepository, Mockito.times(1)).save(any());

        assertNotNull(dtoFrom);
        assertEquals(dtoFrom.getId(), commentDto1.getId());
        assertEquals(dtoFrom.getText(), commentDto1.getText());
        assertEquals(dtoFrom.getAuthorName(), commentDto1.getAuthorName());
    }

    @Test
    void itemMapperToItemInfoDto() {
        ItemInfoDto.BookingInfoDto lastBooking = new ItemInfoDto.BookingInfoDto(1L, 1L);
        ItemInfoDto.BookingInfoDto nextBooking = new ItemInfoDto.BookingInfoDto(2L, 2L);
        CommentDto commentDto = new CommentDto(1L, "testText", "user1", LocalDateTime.now());
        ItemInfoDto expected = new ItemInfoDto(1L, "car", "very fast", true,
                lastBooking,
                nextBooking,
                List.of(commentDto));
        Comment comment = new Comment(1L, "testText", user1, item1, commentDto.getCreated());
        Booking lsBooking = new Booking(1L, null, null, null,
                new User(1L, "dsds", "dsd@mail.ru"), BookingState.WAITING);
        Booking nxBooking = new Booking(2L, null, null, null,
                new User(2L, "dsds", "dsd@mail.ru"), BookingState.WAITING);

        ItemInfoDto response = ItemMapper.toItemInfoDto(item1, List.of(lsBooking, nxBooking),
                List.of(comment));

        assertNotNull(response);
        assertEquals(expected.getId(), response.getId());
        assertEquals(expected.getAvailable(), response.getAvailable());
        assertEquals(expected.getName(), response.getName());
        assertEquals(expected.getDescription(), response.getDescription());
        assertEquals(expected.getLastBooking().getId(), response.getLastBooking().getId());
        assertEquals(expected.getLastBooking().getBookerId(), response.getLastBooking().getBookerId());
        assertEquals(expected.getNextBooking().getId(), response.getNextBooking().getId());
        assertEquals(expected.getNextBooking().getBookerId(), response.getNextBooking().getBookerId());
        assertEquals(expected.getComments().get(0).getAuthorName(), response.getComments().get(0).getAuthorName());
        assertEquals(expected.getComments().get(0).getId(), response.getComments().get(0).getId());
        assertEquals(expected.getComments().get(0).getText(), response.getComments().get(0).getText());
        assertEquals(expected.getComments().get(0).getCreated(), response.getComments().get(0).getCreated());
    }
}
