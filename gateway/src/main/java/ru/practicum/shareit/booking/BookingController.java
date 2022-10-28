package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.Create;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.error.InvalidArgumentException;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
    private final BookingClient bookingClient;

    @GetMapping
    public ResponseEntity<Object> getAllByBookerId(@RequestHeader("X-Sharer-User-Id") long userId,
                                                   @RequestParam(name = "state", defaultValue = "ALL") String stateParam,
                                                   @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                   @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        BookingSearchState bookingSearchState = null;
        try {
            bookingSearchState = BookingSearchState.valueOf(stateParam);
        } catch (IllegalArgumentException e) {
            throw new InvalidArgumentException("Unknown state: UNSUPPORTED_STATUS");
        }
        log.info("Get booking with state {}, userId={}, from={}, size={}", stateParam, userId, from, size);
        return bookingClient.getAllByBookerId(userId, bookingSearchState, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getAllByOwnerId(@RequestHeader("X-Sharer-User-Id") long userId,
                                                  @RequestParam(name = "state", defaultValue = "ALL") String stateParam,
                                                  @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                  @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        BookingSearchState bookingSearchState = null;
        try {
            bookingSearchState = BookingSearchState.valueOf(stateParam);
        } catch (IllegalArgumentException e) {
            throw new InvalidArgumentException("Unknown state: UNSUPPORTED_STATUS");
        }
        log.info("Controller = {}, UserId = {} ,get all Booking by owner with State = {}, from = {} and size = {}",
                this.getClass().getSimpleName(), userId, bookingSearchState, from, size);
        return bookingClient.getAllByOwnerId(userId, bookingSearchState, from, size);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getById(@RequestHeader("X-Sharer-User-Id") long userId,
                                          @PathVariable Long bookingId) {
        log.info("Controller = {}, UserId = {} ,get Booking = {} ", this.getClass().getSimpleName(),
                userId, bookingId);
        return bookingClient.getById(userId, bookingId);
    }

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader("X-Sharer-User-Id") long userId,
                                         @Validated({Create.class}) @RequestBody BookingDto bookingDto) {
        log.info("Controller = {}, UserId = {} ,Create Booking = {}", this.getClass().getSimpleName(),
                userId, bookingDto);
        return bookingClient.create(userId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> updateState(@RequestHeader("X-Sharer-User-Id") long userId,
                                              @PathVariable long bookingId,
                                              @RequestParam Boolean approved) {
        log.info("Controller = {}, UserId = {} ,update Booking = {} if approved = {}", this.getClass().getSimpleName(),
                userId, bookingId, approved);
        return bookingClient.updateState(userId, bookingId, approved);
    }
}
