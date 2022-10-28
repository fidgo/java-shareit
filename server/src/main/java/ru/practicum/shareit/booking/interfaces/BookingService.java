package ru.practicum.shareit.booking.interfaces;

import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.BookingSearchState;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingUpdateDto;

import java.util.List;

public interface BookingService {
    BookingDto create(long userId, BookingDto bookingDto);

    BookingUpdateDto updateState(long userId, long bookingId, Boolean approved);

    BookingUpdateDto getById(long userId, long bookingId);

    List<BookingUpdateDto> getAllByBookerId(long userId, BookingSearchState bookingSearchState,
                                            PageRequest pageRequest);

    List<BookingUpdateDto> getAllByOwnerId(long userId, BookingSearchState bookingSearchStateState,
                                           PageRequest pageRequest);

}
