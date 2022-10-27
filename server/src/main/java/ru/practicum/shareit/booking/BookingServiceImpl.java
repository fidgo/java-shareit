package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingUpdateDto;
import ru.practicum.shareit.booking.interfaces.BookingRepository;
import ru.practicum.shareit.booking.interfaces.BookingService;
import ru.practicum.shareit.error.*;
import ru.practicum.shareit.item.interfaces.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.interfaces.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;

    @Override
    @Transactional
    public BookingDto create(long userId, BookingDto bookingDto) {
        User booker = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElemException("Нет такого пользователя"));
        Item item = itemRepository.findById(bookingDto.getItemId())
                .orElseThrow(() -> new NoSuchElemException("Нет такого предмета"));
        if (!item.getAvailable()) {
            throw new StatusElemException("Предмет недоступен");
        }
        if (item.getOwner().getId() == userId) {
            throw new InvalidAccessException("Нельзя забронировать свой предмет");
        }

        Booking booking = BookingMapper.toBooking(bookingDto, booker, item);
        booking.setStatus(BookingState.WAITING);
        booking = bookingRepository.save(booking);

        return BookingMapper.toBookingDto(booking);
    }

    @Override
    @Transactional
    public BookingUpdateDto updateState(long userId, long bookingId, Boolean approved) {
        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElemException("Нет такого пользователя"));

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NoSuchElemException("Нет такого бронирования"));

        if (booking.getItem().getOwner().getId() != userId) {
            throw new InvalidBookerExcception("Подтверждение или отклонение запроса на бронирование может исполнить" +
                    " только владелец вещи");
        }

        BookingState newBookingState = approved ? BookingState.APPROVED : BookingState.REJECTED;

        if ((booking.getStatus() == BookingState.APPROVED) && (newBookingState == BookingState.APPROVED)) {
            throw new AlreadySetStatusException("Уже APPROVED установлен");
        }

        booking.setStatus(newBookingState);
        Booking newBooking = bookingRepository.save(booking);

        return BookingMapper.toBookingUpdateDto(newBooking);
    }

    @Override
    @Transactional
    public BookingUpdateDto getById(long userId, long bookingId) {
        User userById = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElemException("Нет такого пользователя"));

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NoSuchElemException("Нет такого бронирования"));

        boolean isUserIdOwner = booking.getItem().getOwner().getId() == userId;
        boolean isUserIdBooker = booking.getBooker().getId() == userId;

        if (!(isUserIdOwner || isUserIdBooker)) {
            throw new NoSuchElemException("Получение данных о конкретном бронировании (включая его статус) " +
                    "может быть выполнено либо автором бронирования, либо владельцем вещи, к которой относится" +
                    " бронирование.");
        }

        return BookingMapper.toBookingUpdateDto(booking);
    }

    @Override
    @Transactional
    public List<BookingUpdateDto> getAllByBookerId(long userId, BookingSearchState bookingSearchState,
                                                   PageRequest pageRequest) {
        User userById = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElemException("Нет такого пользователя"));
        boolean isValidState = isBookingSearchStateValid(bookingSearchState);
        if (!isValidState) {
            throw new NoSuchElemException("Такой статус брони не поддерживается");
        }

        Page<Booking> bookingPage = getUsersBookingsByStateFromBookerWithPage(userId, bookingSearchState, pageRequest);

        List<Booking> bookings = bookingPage.stream().collect(Collectors.toList());
        return BookingMapper.bookingUpdateDtoList(bookings);
    }

    @Override
    @Transactional
    public List<BookingUpdateDto> getAllByOwnerId(long userId, BookingSearchState bookingSearchState,
                                                  PageRequest pageRequest) {
        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElemException("Нет такого пользователя"));
        boolean isValidState = isBookingSearchStateValid(bookingSearchState);
        if (!isValidState) {
            throw new NoSuchElemException("Такой статус брони не поддерживается");
        }
        if (!hasOwnerByIdAtLeastOneItem(owner)) {
            throw new NoSuchElemException("У владельца " + userId + " нет ни одного предмета");
        }

        Page<Booking> bookingPage = getUsersBookingsByStateFromOwner(userId, bookingSearchState, pageRequest);
        List<Booking> bookings = bookingPage.stream().collect(Collectors.toList());
        return BookingMapper.bookingUpdateDtoList(bookings);
    }

    private Page<Booking> getUsersBookingsByStateFromOwner(long userId, BookingSearchState bookingSearchState,
                                                           PageRequest pageRequest) {
        Page<Booking> bookings = null;
        switch (bookingSearchState) {
            case ALL:
                bookings = bookingRepository.findAllByItem_Owner_Id(userId, pageRequest);
                break;
            case APPROVED:
            case WAITING:
            case REJECTED:
                BookingState bookingState = convertBookingSearchStateToBookingState(bookingSearchState);
                bookings =
                        bookingRepository.findAllByItem_Owner_IdAndStatusIs(userId, bookingState, pageRequest);
                break;
            case FUTURE:
                bookings =
                        bookingRepository.findAllByItem_Owner_IdAndStartAfter(userId, LocalDateTime.now(), pageRequest);
                break;
            case PAST:
                bookings =
                        bookingRepository.findAllByItem_Owner_IdAndEndBefore(userId,
                                LocalDateTime.now(), pageRequest);
                break;
            case CURRENT:
                bookings = bookingRepository.findCurrentBookingFromOwner(userId, LocalDateTime.now(), pageRequest);
                break;
        }
        return bookings;
    }

    private boolean hasOwnerByIdAtLeastOneItem(User owner) {
        Optional<Item> first = itemRepository.findFirstByOwner(owner);
        return first.isPresent();
    }

    private boolean isBookingSearchStateValid(BookingSearchState bookingSearchState) {
        return bookingSearchState == BookingSearchState.ALL
                || bookingSearchState == BookingSearchState.CURRENT
                || bookingSearchState == BookingSearchState.PAST
                || bookingSearchState == BookingSearchState.FUTURE
                || bookingSearchState == BookingSearchState.WAITING
                || bookingSearchState == BookingSearchState.REJECTED;
    }


    private Page<Booking> getUsersBookingsByStateFromBookerWithPage(long userId, BookingSearchState bookingSearchState,
                                                                    PageRequest pageRequest) {
        Page<Booking> bookings = null;
        switch (bookingSearchState) {
            case ALL:
                bookings = bookingRepository.findAllByBooker_Id(userId, pageRequest);
                break;
            case APPROVED:
            case WAITING:
            case REJECTED:
                BookingState bookingState = convertBookingSearchStateToBookingState(bookingSearchState);
                bookings =
                        bookingRepository.findAllByBooker_IdAndStatusIs(userId, bookingState, pageRequest);
                break;
            case FUTURE:
                bookings =
                        bookingRepository.findAllByBooker_IdAndStartAfter(userId, LocalDateTime.now(), pageRequest);
                break;
            case PAST:
                bookings =
                        bookingRepository.findAllByBooker_IdAndEndBefore(userId, LocalDateTime.now(), pageRequest);
                break;
            case CURRENT:
                bookings = bookingRepository.findCurrentBookingFromBooker(userId, LocalDateTime.now(), pageRequest);
                break;
        }
        return bookings;
    }


    private BookingState convertBookingSearchStateToBookingState(BookingSearchState bookingSearchState) {
        BookingState bookingState = null;
        try {
            bookingState = BookingState.valueOf(bookingSearchState.toString());
        } catch (IllegalArgumentException e) {
            throw new InvalidArgumentException("Unvalid conversion " + bookingSearchState);
        }
        return bookingState;
    }


}
