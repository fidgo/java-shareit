package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.PageRequestFrom;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingUpdateDto;
import ru.practicum.shareit.booking.interfaces.BookingRepository;
import ru.practicum.shareit.booking.interfaces.BookingService;
import ru.practicum.shareit.error.*;
import ru.practicum.shareit.item.interfaces.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.interfaces.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

public class BookingServiceTest {
    private BookingService bookingService;
    private UserRepository userRepository;
    private ItemRepository itemRepository;
    private BookingRepository bookingRepository;

    private Booking booking1;
    private Booking booking12;

    private BookingDto booking1Dto;

    private User user1;

    private User user2;
    private Item item1;
    private Item item2;

    @BeforeEach
    void beforeEach() {
        userRepository = mock(UserRepository.class);
        itemRepository = mock(ItemRepository.class);
        bookingRepository = mock(BookingRepository.class);
        bookingService = new BookingServiceImpl(userRepository, itemRepository, bookingRepository);

        user2 = new User(2L, "user2", "user2@mail.ru");
        item2 = new Item(2L, "ball", "round object", true, user2, null);


        user1 = new User(1L, "user1", "user1@mail.ru");
        item1 = new Item(1L, "car", "very fast", true, user1, null);
        booking1 = new Booking(1L, null, null, item1, user1, BookingState.WAITING);
        booking12 = new Booking(2L, null, null, item2, user1, BookingState.WAITING);
        booking1Dto = new BookingDto(1L, 1L, null, null, BookingState.WAITING,
                new BookingDto.UserBookingDto(1L, "user1", "user1@mail.ru"),
                new BookingDto.ItemBookingDto(1L, "car", "very fast", true)
        );
    }

    @Test
    void create() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user1));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item1));
        when(bookingRepository.save(any())).thenReturn(booking1);

        BookingDto dtoFrom = bookingService.create(5L, booking1Dto);
        verify(userRepository, Mockito.times(1)).findById(anyLong());
        verify(itemRepository, Mockito.times(1)).findById(anyLong());
        verify(bookingRepository, Mockito.times(1)).save(any());

        assertNotNull(dtoFrom);
        assertEquals(booking1Dto.getId(), dtoFrom.getId());
        assertEquals(booking1Dto.getItemId(), dtoFrom.getItemId());
        assertEquals(booking1Dto.getBooker().getName(), dtoFrom.getBooker().getName());
        assertEquals(booking1Dto.getBooker().getId(), dtoFrom.getBooker().getId());
        assertEquals(booking1Dto.getBooker().getEmail(), dtoFrom.getBooker().getEmail());
        assertEquals(booking1Dto.getStatus(), dtoFrom.getStatus());
        assertEquals(booking1Dto.getItem().getId(), dtoFrom.getItem().getId());
        assertEquals(booking1Dto.getItem().getName(), dtoFrom.getItem().getName());
        assertEquals(booking1Dto.getItem().getDescription(), dtoFrom.getItem().getDescription());
    }

    @Test
    void createNoSuchItem() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user1));
        when(itemRepository.findById(eq(4L))).thenReturn(Optional.of(item1));

        NoSuchElemException ex = assertThrows(NoSuchElemException.class,
                () -> {
                    bookingService.create(5L, booking1Dto);
                });
        verify(userRepository, Mockito.times(1)).findById(anyLong());
        verify(itemRepository, Mockito.times(1)).findById(anyLong());
    }

    @Test
    void createNoSuchUser() {
        when(userRepository.findById(eq(1L))).thenReturn(Optional.of(user1));
        NoSuchElemException ex = assertThrows(NoSuchElemException.class,
                () -> {
                    bookingService.create(5L, booking1Dto);
                });
        verify(userRepository, Mockito.times(1)).findById(anyLong());
    }

    @Test
    void createInvalidAccessEx() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user1));
        item1.setOwner(user1);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item1));

        InvalidAccessException ex = assertThrows(InvalidAccessException.class,
                () -> {
                    bookingService.create(1L, booking1Dto);
                });
        assertEquals("Нельзя забронировать свой предмет", ex.getMessage());
        verify(userRepository, Mockito.times(1)).findById(anyLong());
        verify(itemRepository, Mockito.times(1)).findById(anyLong());
    }

    @Test
    void createStatusElemExc() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user1));
        item1.setAvailable(false);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item1));

        StatusElemException ex = assertThrows(StatusElemException.class,
                () -> {
                    bookingService.create(1L, booking1Dto);
                });
        assertEquals("Предмет недоступен", ex.getMessage());
        verify(userRepository, Mockito.times(1)).findById(anyLong());
        verify(itemRepository, Mockito.times(1)).findById(anyLong());
    }


    @Test
    void updateState() {
        Booking booking1appr = new Booking(1L, null, null, item1, user1, BookingState.APPROVED);
        BookingDto booking1DtoApr = new BookingDto(1L, 1L, null, null, BookingState.APPROVED,
                new BookingDto.UserBookingDto(1L, "user1", "user1@mail.ru"),
                new BookingDto.ItemBookingDto(1L, "car", "very fast", true)
        );
        BookingUpdateDto bookingUpdateDtoAppr = new BookingUpdateDto(
                1L,
                BookingState.APPROVED,
                new BookingUpdateDto.UserBookingUpdateDto(user1.getId(), user1.getName(), user1.getEmail()),
                new BookingUpdateDto.ItemBookingUpdateDto(item1.getId(), item1.getName(), item1.getDescription(), item1.getAvailable()),
                null,
                null
        );

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user1));
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking1));
        when(bookingRepository.save(any())).thenReturn(booking1appr);

        BookingUpdateDto bookingUpdateDto = bookingService.updateState(1L, 1L, true);
        verify(userRepository, Mockito.times(1)).findById(anyLong());
        verify(bookingRepository, Mockito.times(1)).findById(anyLong());
        verify(bookingRepository, Mockito.times(1)).save(any());
        assertNotNull(bookingUpdateDto);
        assertEquals(bookingUpdateDtoAppr.getId(), bookingUpdateDto.getId());
        assertEquals(bookingUpdateDtoAppr.getStatus(), bookingUpdateDto.getStatus());
        assertEquals(bookingUpdateDtoAppr.getBooker().getId(), bookingUpdateDto.getBooker().getId());
        assertEquals(bookingUpdateDtoAppr.getBooker().getName(), bookingUpdateDto.getBooker().getName());
        assertEquals(bookingUpdateDtoAppr.getBooker().getEmail(), bookingUpdateDto.getBooker().getEmail());
        assertEquals(bookingUpdateDtoAppr.getItem().getId(), bookingUpdateDto.getItem().getId());
        assertEquals(bookingUpdateDtoAppr.getItem().getAvailable(), bookingUpdateDto.getItem().getAvailable());
        assertEquals(bookingUpdateDtoAppr.getItem().getDescription(), bookingUpdateDto.getItem().getDescription());
    }

    @Test
    void updateStateInvalidBookerEx() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user1));
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking1));

        InvalidBookerExcception ex = assertThrows(InvalidBookerExcception.class,
                () -> {
                    bookingService.updateState(2L, 1L, true);
                });
        assertEquals("Подтверждение или отклонение запроса на бронирование может исполнить" +
                " только владелец вещи", ex.getMessage());

        verify(userRepository, Mockito.times(1)).findById(anyLong());
        verify(bookingRepository, Mockito.times(1)).findById(anyLong());
    }


    @Test
    void updateStateAlreadySetStatusEx() {
        booking1.setStatus(BookingState.APPROVED);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user1));
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking1));

        AlreadySetStatusException ex = assertThrows(AlreadySetStatusException.class,
                () -> {
                    bookingService.updateState(1L, 1L, true);
                });
        assertEquals("Уже APPROVED установлен", ex.getMessage());

        verify(userRepository, Mockito.times(1)).findById(anyLong());
        verify(bookingRepository, Mockito.times(1)).findById(anyLong());
    }


    @Test
    void getById() {

        BookingUpdateDto bookingUpdateDtoAppr = new BookingUpdateDto(
                1L,
                BookingState.WAITING,
                new BookingUpdateDto.UserBookingUpdateDto(user1.getId(), user1.getName(), user1.getEmail()),
                new BookingUpdateDto.ItemBookingUpdateDto(item1.getId(), item1.getName(), item1.getDescription(), item1.getAvailable()),
                null,
                null
        );

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user1));
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking1));

        BookingUpdateDto bookingUpdateDto = bookingService.getById(1L, 1L);
        verify(userRepository, Mockito.times(1)).findById(anyLong());
        verify(bookingRepository, Mockito.times(1)).findById(anyLong());
        assertNotNull(bookingUpdateDto);
        assertEquals(bookingUpdateDtoAppr.getId(), bookingUpdateDto.getId());
        assertEquals(bookingUpdateDtoAppr.getStatus(), bookingUpdateDto.getStatus());
        assertEquals(bookingUpdateDtoAppr.getBooker().getId(), bookingUpdateDto.getBooker().getId());
        assertEquals(bookingUpdateDtoAppr.getBooker().getName(), bookingUpdateDto.getBooker().getName());
        assertEquals(bookingUpdateDtoAppr.getBooker().getEmail(), bookingUpdateDto.getBooker().getEmail());
        assertEquals(bookingUpdateDtoAppr.getItem().getId(), bookingUpdateDto.getItem().getId());
        assertEquals(bookingUpdateDtoAppr.getItem().getAvailable(), bookingUpdateDto.getItem().getAvailable());
        assertEquals(bookingUpdateDtoAppr.getItem().getDescription(), bookingUpdateDto.getItem().getDescription());
    }

    @Test
    void getByIdNoSuchEx() {

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user1));
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking1));

        NoSuchElemException ex =
                assertThrows(NoSuchElemException.class, () -> {
                    bookingService.getById(2L, 1L);
                });

        assertEquals("Получение данных о конкретном бронировании (включая его статус) может быть выполнено" +
                " либо автором бронирования, либо владельцем вещи, к которой относится бронирование.", ex.getMessage());
    }

    @Test
    void getAllByOwnerIdNoSuchEx() {
        PageRequest pageRequest = new PageRequestFrom(10, 0, Sort.by("start").descending());

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user1));
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking1));

        NoSuchElemException ex =
                assertThrows(NoSuchElemException.class, () -> {
                    bookingService.getAllByOwnerId(2L,
                            BookingSearchState.ALL, pageRequest);
                });

        assertEquals("У владельца 2 нет ни одного предмета", ex.getMessage());
    }

    @Test
    void getAllByOwnerIdStateWaiting() {
        BookingUpdateDto bookingUpdateDtoAppr = new BookingUpdateDto(
                2L,
                BookingState.WAITING,
                new BookingUpdateDto.UserBookingUpdateDto(user1.getId(), user1.getName(), user1.getEmail()),
                new BookingUpdateDto.ItemBookingUpdateDto(item2.getId(), item2.getName(), item2.getDescription(),
                        item2.getAvailable()),
                null,
                null
        );
        PageImpl<Booking> pgBooking = new PageImpl<>(List.of(booking12));
        int size = 10;
        int from = 0;
        PageRequest pageRequest = new PageRequestFrom(size, from, Sort.by("start").descending());
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user2));
        when(itemRepository.findFirstByOwner(any())).thenReturn(Optional.of(item2));
        when(bookingRepository.findAllByItem_Owner_IdAndStatusIs(anyLong(), any(), any())).thenReturn(pgBooking);

        List<BookingUpdateDto> bookingUpdateDtoList =
                bookingService.getAllByOwnerId(2L, BookingSearchState.WAITING, pageRequest);
        verify(userRepository, Mockito.times(1)).findById(anyLong());
        verify(itemRepository, Mockito.times(1)).findFirstByOwner(any());
        verify(bookingRepository, Mockito.times(1))
                .findAllByItem_Owner_IdAndStatusIs(anyLong(), any(), any());
        assertNotNull(bookingUpdateDtoList);
        assertEquals(1, bookingUpdateDtoList.size());
        BookingUpdateDto bookingUpdateDto = bookingUpdateDtoList.get(0);
        assertEquals(bookingUpdateDtoAppr.getId(), bookingUpdateDto.getId());
        assertEquals(bookingUpdateDtoAppr.getStatus(), bookingUpdateDto.getStatus());
        assertEquals(bookingUpdateDtoAppr.getBooker().getId(), bookingUpdateDto.getBooker().getId());
        assertEquals(bookingUpdateDtoAppr.getBooker().getName(), bookingUpdateDto.getBooker().getName());
        assertEquals(bookingUpdateDtoAppr.getBooker().getEmail(), bookingUpdateDto.getBooker().getEmail());
        assertEquals(bookingUpdateDtoAppr.getItem().getId(), bookingUpdateDto.getItem().getId());
        assertEquals(bookingUpdateDtoAppr.getItem().getAvailable(), bookingUpdateDto.getItem().getAvailable());
        assertEquals(bookingUpdateDtoAppr.getItem().getDescription(), bookingUpdateDto.getItem().getDescription());
    }

    @Test
    void getAllByOwnerIdStateFuture() {
        BookingUpdateDto bookingUpdateDtoAppr = new BookingUpdateDto(
                2L,
                BookingState.WAITING,
                new BookingUpdateDto.UserBookingUpdateDto(user1.getId(), user1.getName(), user1.getEmail()),
                new BookingUpdateDto.ItemBookingUpdateDto(item2.getId(), item2.getName(), item2.getDescription(),
                        item2.getAvailable()),
                null,
                null
        );
        PageImpl<Booking> pgBooking = new PageImpl<>(List.of(booking12));
        int size = 10;
        int from = 0;
        PageRequest pageRequest = new PageRequestFrom(size, from, Sort.by("start").descending());
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user2));
        when(itemRepository.findFirstByOwner(any())).thenReturn(Optional.of(item2));
        when(bookingRepository.findAllByItem_Owner_IdAndStartAfter(anyLong(), any(), any())).thenReturn(pgBooking);

        List<BookingUpdateDto> bookingUpdateDtoList =
                bookingService.getAllByOwnerId(2L, BookingSearchState.FUTURE, pageRequest);
        verify(userRepository, Mockito.times(1)).findById(anyLong());
        verify(itemRepository, Mockito.times(1)).findFirstByOwner(any());
        verify(bookingRepository, Mockito.times(1))
                .findAllByItem_Owner_IdAndStartAfter(anyLong(), any(), any());
        assertNotNull(bookingUpdateDtoList);
        assertEquals(1, bookingUpdateDtoList.size());
        BookingUpdateDto bookingUpdateDto = bookingUpdateDtoList.get(0);
        assertEquals(bookingUpdateDtoAppr.getId(), bookingUpdateDto.getId());
        assertEquals(bookingUpdateDtoAppr.getStatus(), bookingUpdateDto.getStatus());
        assertEquals(bookingUpdateDtoAppr.getBooker().getId(), bookingUpdateDto.getBooker().getId());
        assertEquals(bookingUpdateDtoAppr.getBooker().getName(), bookingUpdateDto.getBooker().getName());
        assertEquals(bookingUpdateDtoAppr.getBooker().getEmail(), bookingUpdateDto.getBooker().getEmail());
        assertEquals(bookingUpdateDtoAppr.getItem().getId(), bookingUpdateDto.getItem().getId());
        assertEquals(bookingUpdateDtoAppr.getItem().getAvailable(), bookingUpdateDto.getItem().getAvailable());
        assertEquals(bookingUpdateDtoAppr.getItem().getDescription(), bookingUpdateDto.getItem().getDescription());
    }

    @Test
    void getAllByOwnerIdStatePast() {
        BookingUpdateDto bookingUpdateDtoAppr = new BookingUpdateDto(
                2L,
                BookingState.WAITING,
                new BookingUpdateDto.UserBookingUpdateDto(user1.getId(), user1.getName(), user1.getEmail()),
                new BookingUpdateDto.ItemBookingUpdateDto(item2.getId(), item2.getName(), item2.getDescription(),
                        item2.getAvailable()),
                null,
                null
        );
        PageImpl<Booking> pgBooking = new PageImpl<>(List.of(booking12));
        int size = 10;
        int from = 0;
        PageRequest pageRequest = new PageRequestFrom(size, from, Sort.by("start").descending());
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user2));
        when(itemRepository.findFirstByOwner(any())).thenReturn(Optional.of(item2));
        when(bookingRepository.findAllByItem_Owner_IdAndEndBefore(anyLong(), any(), any())).thenReturn(pgBooking);

        List<BookingUpdateDto> bookingUpdateDtoList =
                bookingService.getAllByOwnerId(2L, BookingSearchState.PAST, pageRequest);
        verify(userRepository, Mockito.times(1)).findById(anyLong());
        verify(itemRepository, Mockito.times(1)).findFirstByOwner(any());
        verify(bookingRepository, Mockito.times(1))
                .findAllByItem_Owner_IdAndEndBefore(anyLong(), any(), any());
        assertNotNull(bookingUpdateDtoList);
        assertEquals(1, bookingUpdateDtoList.size());
        BookingUpdateDto bookingUpdateDto = bookingUpdateDtoList.get(0);
        assertEquals(bookingUpdateDtoAppr.getId(), bookingUpdateDto.getId());
        assertEquals(bookingUpdateDtoAppr.getStatus(), bookingUpdateDto.getStatus());
        assertEquals(bookingUpdateDtoAppr.getBooker().getId(), bookingUpdateDto.getBooker().getId());
        assertEquals(bookingUpdateDtoAppr.getBooker().getName(), bookingUpdateDto.getBooker().getName());
        assertEquals(bookingUpdateDtoAppr.getBooker().getEmail(), bookingUpdateDto.getBooker().getEmail());
        assertEquals(bookingUpdateDtoAppr.getItem().getId(), bookingUpdateDto.getItem().getId());
        assertEquals(bookingUpdateDtoAppr.getItem().getAvailable(), bookingUpdateDto.getItem().getAvailable());
        assertEquals(bookingUpdateDtoAppr.getItem().getDescription(), bookingUpdateDto.getItem().getDescription());
    }

    @Test
    void getAllByOwnerIdStateCurrent() {
        BookingUpdateDto bookingUpdateDtoAppr = new BookingUpdateDto(
                2L,
                BookingState.WAITING,
                new BookingUpdateDto.UserBookingUpdateDto(user1.getId(), user1.getName(), user1.getEmail()),
                new BookingUpdateDto.ItemBookingUpdateDto(item2.getId(), item2.getName(), item2.getDescription(),
                        item2.getAvailable()),
                null,
                null
        );
        PageImpl<Booking> pgBooking = new PageImpl<>(List.of(booking12));
        int size = 10;
        int from = 0;
        PageRequest pageRequest = new PageRequestFrom(size, from, Sort.by("start").descending());
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user2));
        when(itemRepository.findFirstByOwner(any())).thenReturn(Optional.of(item2));
        when(bookingRepository.findCurrentBookingFromOwner(anyLong(), any(), any())).thenReturn(pgBooking);

        List<BookingUpdateDto> bookingUpdateDtoList =
                bookingService.getAllByOwnerId(2L, BookingSearchState.CURRENT, pageRequest);
        verify(userRepository, Mockito.times(1)).findById(anyLong());
        verify(itemRepository, Mockito.times(1)).findFirstByOwner(any());
        verify(bookingRepository, Mockito.times(1))
                .findCurrentBookingFromOwner(anyLong(), any(), any());
        assertNotNull(bookingUpdateDtoList);
        assertEquals(1, bookingUpdateDtoList.size());
        BookingUpdateDto bookingUpdateDto = bookingUpdateDtoList.get(0);
        assertEquals(bookingUpdateDtoAppr.getId(), bookingUpdateDto.getId());
        assertEquals(bookingUpdateDtoAppr.getStatus(), bookingUpdateDto.getStatus());
        assertEquals(bookingUpdateDtoAppr.getBooker().getId(), bookingUpdateDto.getBooker().getId());
        assertEquals(bookingUpdateDtoAppr.getBooker().getName(), bookingUpdateDto.getBooker().getName());
        assertEquals(bookingUpdateDtoAppr.getBooker().getEmail(), bookingUpdateDto.getBooker().getEmail());
        assertEquals(bookingUpdateDtoAppr.getItem().getId(), bookingUpdateDto.getItem().getId());
        assertEquals(bookingUpdateDtoAppr.getItem().getAvailable(), bookingUpdateDto.getItem().getAvailable());
        assertEquals(bookingUpdateDtoAppr.getItem().getDescription(), bookingUpdateDto.getItem().getDescription());
    }


    @Test
    void getAllByBookerIdStateAll() {
        BookingUpdateDto bookingUpdateDtoAppr = new BookingUpdateDto(
                1L,
                BookingState.WAITING,
                new BookingUpdateDto.UserBookingUpdateDto(user1.getId(), user1.getName(), user1.getEmail()),
                new BookingUpdateDto.ItemBookingUpdateDto(item1.getId(), item1.getName(), item1.getDescription(),
                        item1.getAvailable()),
                null,
                null
        );
        PageImpl<Booking> pgBooking = new PageImpl<>(List.of(booking1));
        int size = 10;
        int from = 0;
        PageRequest pageRequest = new PageRequestFrom(size, from, Sort.by("start").descending());
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user1));
        when(bookingRepository.findAllByBooker_Id(anyLong(), any())).thenReturn(pgBooking);

        List<BookingUpdateDto> bookingUpdateDtoList =
                bookingService.getAllByBookerId(1L, BookingSearchState.ALL, pageRequest);
        verify(userRepository, Mockito.times(1)).findById(anyLong());
        verify(bookingRepository, Mockito.times(1)).findAllByBooker_Id(anyLong(), any());
        assertNotNull(bookingUpdateDtoList);
        assertEquals(1, bookingUpdateDtoList.size());
        BookingUpdateDto bookingUpdateDto = bookingUpdateDtoList.get(0);
        assertEquals(bookingUpdateDtoAppr.getId(), bookingUpdateDto.getId());
        assertEquals(bookingUpdateDtoAppr.getStatus(), bookingUpdateDto.getStatus());
        assertEquals(bookingUpdateDtoAppr.getBooker().getId(), bookingUpdateDto.getBooker().getId());
        assertEquals(bookingUpdateDtoAppr.getBooker().getName(), bookingUpdateDto.getBooker().getName());
        assertEquals(bookingUpdateDtoAppr.getBooker().getEmail(), bookingUpdateDto.getBooker().getEmail());
        assertEquals(bookingUpdateDtoAppr.getItem().getId(), bookingUpdateDto.getItem().getId());
        assertEquals(bookingUpdateDtoAppr.getItem().getAvailable(), bookingUpdateDto.getItem().getAvailable());
        assertEquals(bookingUpdateDtoAppr.getItem().getDescription(), bookingUpdateDto.getItem().getDescription());
    }

    @Test
    void getAllByBookerIdStateWaiting() {
        BookingUpdateDto bookingUpdateDtoAppr = new BookingUpdateDto(
                1L,
                BookingState.WAITING,
                new BookingUpdateDto.UserBookingUpdateDto(user1.getId(), user1.getName(), user1.getEmail()),
                new BookingUpdateDto.ItemBookingUpdateDto(item1.getId(), item1.getName(), item1.getDescription(),
                        item1.getAvailable()),
                null,
                null
        );
        PageImpl<Booking> pgBooking = new PageImpl<>(List.of(booking1));
        int size = 10;
        int from = 0;
        PageRequest pageRequest = new PageRequestFrom(size, from, Sort.by("start").descending());
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user1));
        when(bookingRepository.findAllByBooker_IdAndStatusIs(anyLong(), any(), any())).thenReturn(pgBooking);

        List<BookingUpdateDto> bookingUpdateDtoList =
                bookingService.getAllByBookerId(1L, BookingSearchState.WAITING, pageRequest);
        verify(userRepository, Mockito.times(1)).findById(anyLong());
        verify(bookingRepository, Mockito.times(1))
                .findAllByBooker_IdAndStatusIs(anyLong(), any(), any());
        assertNotNull(bookingUpdateDtoList);
        assertEquals(1, bookingUpdateDtoList.size());
        BookingUpdateDto bookingUpdateDto = bookingUpdateDtoList.get(0);
        assertEquals(bookingUpdateDtoAppr.getId(), bookingUpdateDto.getId());
        assertEquals(bookingUpdateDtoAppr.getStatus(), bookingUpdateDto.getStatus());
        assertEquals(bookingUpdateDtoAppr.getBooker().getId(), bookingUpdateDto.getBooker().getId());
        assertEquals(bookingUpdateDtoAppr.getBooker().getName(), bookingUpdateDto.getBooker().getName());
        assertEquals(bookingUpdateDtoAppr.getBooker().getEmail(), bookingUpdateDto.getBooker().getEmail());
        assertEquals(bookingUpdateDtoAppr.getItem().getId(), bookingUpdateDto.getItem().getId());
        assertEquals(bookingUpdateDtoAppr.getItem().getAvailable(), bookingUpdateDto.getItem().getAvailable());
        assertEquals(bookingUpdateDtoAppr.getItem().getDescription(), bookingUpdateDto.getItem().getDescription());
    }

    @Test
    void getAllByBookerIdStateFuture() {
        BookingUpdateDto bookingUpdateDtoAppr = new BookingUpdateDto(
                1L,
                BookingState.WAITING,
                new BookingUpdateDto.UserBookingUpdateDto(user1.getId(), user1.getName(), user1.getEmail()),
                new BookingUpdateDto.ItemBookingUpdateDto(item1.getId(), item1.getName(), item1.getDescription(),
                        item1.getAvailable()),
                null,
                null
        );
        PageImpl<Booking> pgBooking = new PageImpl<>(List.of(booking1));
        int size = 10;
        int from = 0;
        PageRequest pageRequest = new PageRequestFrom(size, from, Sort.by("start").descending());
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user1));
        when(bookingRepository.findAllByBooker_IdAndStartAfter(anyLong(), any(), any())).thenReturn(pgBooking);

        List<BookingUpdateDto> bookingUpdateDtoList =
                bookingService.getAllByBookerId(1L, BookingSearchState.FUTURE, pageRequest);
        verify(userRepository, Mockito.times(1)).findById(anyLong());
        verify(bookingRepository, Mockito.times(1))
                .findAllByBooker_IdAndStartAfter(anyLong(), any(), any());
        assertNotNull(bookingUpdateDtoList);
        assertEquals(1, bookingUpdateDtoList.size());
        BookingUpdateDto bookingUpdateDto = bookingUpdateDtoList.get(0);
        assertEquals(bookingUpdateDtoAppr.getId(), bookingUpdateDto.getId());
        assertEquals(bookingUpdateDtoAppr.getStatus(), bookingUpdateDto.getStatus());
        assertEquals(bookingUpdateDtoAppr.getBooker().getId(), bookingUpdateDto.getBooker().getId());
        assertEquals(bookingUpdateDtoAppr.getBooker().getName(), bookingUpdateDto.getBooker().getName());
        assertEquals(bookingUpdateDtoAppr.getBooker().getEmail(), bookingUpdateDto.getBooker().getEmail());
        assertEquals(bookingUpdateDtoAppr.getItem().getId(), bookingUpdateDto.getItem().getId());
        assertEquals(bookingUpdateDtoAppr.getItem().getAvailable(), bookingUpdateDto.getItem().getAvailable());
        assertEquals(bookingUpdateDtoAppr.getItem().getDescription(), bookingUpdateDto.getItem().getDescription());
    }

    @Test
    void getAllByBookerIdStatePast() {
        BookingUpdateDto bookingUpdateDtoAppr = new BookingUpdateDto(
                1L,
                BookingState.WAITING,
                new BookingUpdateDto.UserBookingUpdateDto(user1.getId(), user1.getName(), user1.getEmail()),
                new BookingUpdateDto.ItemBookingUpdateDto(item1.getId(), item1.getName(), item1.getDescription(),
                        item1.getAvailable()),
                null,
                null
        );
        PageImpl<Booking> pgBooking = new PageImpl<>(List.of(booking1));
        int size = 10;
        int from = 0;
        PageRequest pageRequest = new PageRequestFrom(size, from, Sort.by("start").descending());
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user1));
        when(bookingRepository.findAllByBooker_IdAndEndBefore(anyLong(), any(), any())).thenReturn(pgBooking);

        List<BookingUpdateDto> bookingUpdateDtoList =
                bookingService.getAllByBookerId(1L, BookingSearchState.PAST, pageRequest);
        verify(userRepository, Mockito.times(1)).findById(anyLong());
        verify(bookingRepository, Mockito.times(1))
                .findAllByBooker_IdAndEndBefore(anyLong(), any(), any());
        assertNotNull(bookingUpdateDtoList);
        assertEquals(1, bookingUpdateDtoList.size());
        BookingUpdateDto bookingUpdateDto = bookingUpdateDtoList.get(0);
        assertEquals(bookingUpdateDtoAppr.getId(), bookingUpdateDto.getId());
        assertEquals(bookingUpdateDtoAppr.getStatus(), bookingUpdateDto.getStatus());
        assertEquals(bookingUpdateDtoAppr.getBooker().getId(), bookingUpdateDto.getBooker().getId());
        assertEquals(bookingUpdateDtoAppr.getBooker().getName(), bookingUpdateDto.getBooker().getName());
        assertEquals(bookingUpdateDtoAppr.getBooker().getEmail(), bookingUpdateDto.getBooker().getEmail());
        assertEquals(bookingUpdateDtoAppr.getItem().getId(), bookingUpdateDto.getItem().getId());
        assertEquals(bookingUpdateDtoAppr.getItem().getAvailable(), bookingUpdateDto.getItem().getAvailable());
        assertEquals(bookingUpdateDtoAppr.getItem().getDescription(), bookingUpdateDto.getItem().getDescription());
    }

    @Test
    void getAllByBookerIdStateCurrent() {
        BookingUpdateDto bookingUpdateDtoAppr = new BookingUpdateDto(
                1L,
                BookingState.WAITING,
                new BookingUpdateDto.UserBookingUpdateDto(user1.getId(), user1.getName(), user1.getEmail()),
                new BookingUpdateDto.ItemBookingUpdateDto(item1.getId(), item1.getName(), item1.getDescription(),
                        item1.getAvailable()),
                null,
                null
        );
        PageImpl<Booking> pgBooking = new PageImpl<>(List.of(booking1));
        int size = 10;
        int from = 0;
        PageRequest pageRequest = new PageRequestFrom(size, from, Sort.by("start").descending());
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user1));
        when(bookingRepository.findCurrentBookingFromBooker(anyLong(), any(), any())).thenReturn(pgBooking);

        List<BookingUpdateDto> bookingUpdateDtoList =
                bookingService.getAllByBookerId(1L, BookingSearchState.CURRENT, pageRequest);
        verify(userRepository, Mockito.times(1)).findById(anyLong());
        verify(bookingRepository, Mockito.times(1))
                .findCurrentBookingFromBooker(anyLong(), any(), any());
        assertNotNull(bookingUpdateDtoList);
        assertEquals(1, bookingUpdateDtoList.size());
        BookingUpdateDto bookingUpdateDto = bookingUpdateDtoList.get(0);
        assertEquals(bookingUpdateDtoAppr.getId(), bookingUpdateDto.getId());
        assertEquals(bookingUpdateDtoAppr.getStatus(), bookingUpdateDto.getStatus());
        assertEquals(bookingUpdateDtoAppr.getBooker().getId(), bookingUpdateDto.getBooker().getId());
        assertEquals(bookingUpdateDtoAppr.getBooker().getName(), bookingUpdateDto.getBooker().getName());
        assertEquals(bookingUpdateDtoAppr.getBooker().getEmail(), bookingUpdateDto.getBooker().getEmail());
        assertEquals(bookingUpdateDtoAppr.getItem().getId(), bookingUpdateDto.getItem().getId());
        assertEquals(bookingUpdateDtoAppr.getItem().getAvailable(), bookingUpdateDto.getItem().getAvailable());
        assertEquals(bookingUpdateDtoAppr.getItem().getDescription(), bookingUpdateDto.getItem().getDescription());
    }

    @Test
    void getAllByOwnerIdStateAll() {
        BookingUpdateDto bookingUpdateDtoAppr = new BookingUpdateDto(
                2L,
                BookingState.WAITING,
                new BookingUpdateDto.UserBookingUpdateDto(user1.getId(), user1.getName(), user1.getEmail()),
                new BookingUpdateDto.ItemBookingUpdateDto(item2.getId(), item2.getName(), item2.getDescription(),
                        item2.getAvailable()),
                null,
                null
        );
        PageImpl<Booking> pgBooking = new PageImpl<>(List.of(booking12));
        int size = 10;
        int from = 0;
        PageRequest pageRequest = new PageRequestFrom(size, from, Sort.by("start").descending());
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user2));
        when(itemRepository.findFirstByOwner(any())).thenReturn(Optional.of(item2));
        when(bookingRepository.findAllByItem_Owner_Id(anyLong(), any())).thenReturn(pgBooking);

        List<BookingUpdateDto> bookingUpdateDtoList =
                bookingService.getAllByOwnerId(2L, BookingSearchState.ALL, pageRequest);
        verify(userRepository, Mockito.times(1)).findById(anyLong());
        verify(itemRepository, Mockito.times(1)).findFirstByOwner(any());
        verify(bookingRepository, Mockito.times(1)).findAllByItem_Owner_Id(anyLong(), any());
        assertNotNull(bookingUpdateDtoList);
        assertEquals(1, bookingUpdateDtoList.size());
        BookingUpdateDto bookingUpdateDto = bookingUpdateDtoList.get(0);
        assertEquals(bookingUpdateDtoAppr.getId(), bookingUpdateDto.getId());
        assertEquals(bookingUpdateDtoAppr.getStatus(), bookingUpdateDto.getStatus());
        assertEquals(bookingUpdateDtoAppr.getBooker().getId(), bookingUpdateDto.getBooker().getId());
        assertEquals(bookingUpdateDtoAppr.getBooker().getName(), bookingUpdateDto.getBooker().getName());
        assertEquals(bookingUpdateDtoAppr.getBooker().getEmail(), bookingUpdateDto.getBooker().getEmail());
        assertEquals(bookingUpdateDtoAppr.getItem().getId(), bookingUpdateDto.getItem().getId());
        assertEquals(bookingUpdateDtoAppr.getItem().getAvailable(), bookingUpdateDto.getItem().getAvailable());
        assertEquals(bookingUpdateDtoAppr.getItem().getDescription(), bookingUpdateDto.getItem().getDescription());
    }
}
