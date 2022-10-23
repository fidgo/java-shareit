package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.Create;
import ru.practicum.shareit.PageRequestFrom;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingUpdateDto;
import ru.practicum.shareit.booking.interfaces.BookingService;
import ru.practicum.shareit.error.InvalidArgumentException;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@Slf4j
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @GetMapping("/{bookingId}")
    BookingUpdateDto getById(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long bookingId) {
        log.info("Controller = {}, UserId = {} ,get Booking = {} ", this.getClass().getSimpleName(),
                userId, bookingId);
        BookingUpdateDto dto = bookingService.getById(userId, bookingId);
        return dto;
    }


    @GetMapping
    List<BookingUpdateDto> getAllByBookerId(@RequestHeader("X-Sharer-User-Id") long userId,
                                            @RequestParam(defaultValue = "ALL") String state,
                                            @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                            @Positive @RequestParam(defaultValue = "10") Integer size) {
        log.info("Controller = {}, UserId = {} ,get all Booking with State = {}, from = {} and size = {}",
                this.getClass().getSimpleName(), userId, state, from, size);
        BookingSearchState bookingSearchState = null;
        try {
            bookingSearchState = BookingSearchState.valueOf(state);
        } catch (IllegalArgumentException e) {
            throw new InvalidArgumentException("Unknown state: UNSUPPORTED_STATUS");
        }

        PageRequest pageRequest = new PageRequestFrom(size, from, Sort.by("start").descending());

        List<BookingUpdateDto> dto = bookingService.getAllByBookerId(userId, bookingSearchState, pageRequest);
        return dto;
    }

    @GetMapping("/owner")
    List<BookingUpdateDto> getAllByOwnerId(@RequestHeader("X-Sharer-User-Id") long userId,
                                           @RequestParam(defaultValue = "ALL") String state,
                                           @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                           @Positive @RequestParam(defaultValue = "10") Integer size) {
        log.info("Controller = {}, UserId = {} ,get all Booking by owner with State = {}, from = {} and size = {}",
                this.getClass().getSimpleName(), userId, state, from, size);
        BookingSearchState bookingSearchState = null;
        try {
            bookingSearchState = BookingSearchState.valueOf(state);
        } catch (IllegalArgumentException e) {
            throw new InvalidArgumentException("Unknown state: UNSUPPORTED_STATUS");
        }

        PageRequest pageRequest = new PageRequestFrom(size, from, Sort.by("start").descending());

        List<BookingUpdateDto> dto = bookingService.getAllByOwnerId(userId, bookingSearchState, pageRequest);
        return dto;
    }

    @PostMapping
    BookingDto create(@RequestHeader("X-Sharer-User-Id") long userId,
                      @Validated({Create.class}) @RequestBody BookingDto bookingDto) {
        log.info("Controller = {}, UserId = {} ,Create Booking = {}", this.getClass().getSimpleName(),
                userId, bookingDto);
        BookingDto dto = bookingService.create(userId, bookingDto);
        return dto;
    }

    @PatchMapping("/{bookingId}")
    BookingUpdateDto updateState(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long bookingId,
                                 @RequestParam Boolean approved) {
        log.info("Controller = {}, UserId = {} ,update Booking = {} if approved = {}", this.getClass().getSimpleName(),
                userId, bookingId, approved);
        BookingUpdateDto dto = bookingService.updateState(userId, bookingId, approved);
        return dto;
    }
}
