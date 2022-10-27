package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.Create;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private final RequestClient requestClient;

    @GetMapping
    public ResponseEntity<Object> getOwnRequests(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Controller = {}, UserId = {} , get own requests", this.getClass().getSimpleName(), userId);
        return requestClient.getOwnRequests(userId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequest(@RequestHeader("X-Sharer-User-Id") long userId,
                                             @PathVariable long requestId) {
        log.info("Controller = {}, UserId = {} , get request by id = {}", this.getClass().getSimpleName(),
                userId, requestId);
        return requestClient.getById(userId, requestId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllRequests(@RequestHeader("X-Sharer-User-Id") long userId,
                                                 @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                                 @Positive @RequestParam(defaultValue = "10") Integer size) {
        log.info("Controller = {}, UserId = {} , get all requests from = {} size = {}", this.getClass().getSimpleName(),
                userId, from, size);
        return requestClient.getAllRequests(userId, from, size);
    }

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader("X-Sharer-User-Id") long userId,
                                         @Validated({Create.class}) @RequestBody ItemRequestDto itemRequestDto) {
        log.info("Controller = {}, UserId = {} ,Create ItemRequestDto = {}", this.getClass().getSimpleName(),
                userId, itemRequestDto);
        return requestClient.create(userId, itemRequestDto);
    }

}
