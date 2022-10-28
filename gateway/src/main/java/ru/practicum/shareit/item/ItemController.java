package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.Create;
import ru.practicum.shareit.Update;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {
    private final ItemClient itemClient;

    @GetMapping
    public ResponseEntity<Object> getAll(@RequestHeader("X-Sharer-User-Id") long userId,
                                         @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                         @Positive @RequestParam(defaultValue = "10") Integer size
    ) {
        log.info("Controller = {}, UserId = {} , get all Items from = {} and size = {}",
                this.getClass().getSimpleName(), userId, from, size);
        return itemClient.getAll(userId, from, size);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> get(@RequestHeader("X-Sharer-User-Id") long userId,
                                      @PathVariable long itemId) {
        log.info("Controller = {}, UserId = {} ,get ItemId = {}", this.getClass().getSimpleName(), userId, itemId);
        return itemClient.get(userId, itemId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> search(@RequestHeader("X-Sharer-User-Id") long userId,
                                         @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                         @Positive @RequestParam(defaultValue = "10") Integer size,
                                         @RequestParam(required = false) String text) {
        log.info("Controller = {}, UserId = {} ,get ItemId with text {} from = {} and size = {}",
                this.getClass().getSimpleName(), userId, text, from, size);
        return itemClient.search(userId, text, from, size);
    }

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader("X-Sharer-User-Id") long userId,
                                         @Validated({Create.class}) @RequestBody ItemDto itemDto) {
        log.info("Controller = {}, UserId = {} ,Create Item = {}", this.getClass().getSimpleName(), userId, itemDto);
        return itemClient.create(userId, itemDto);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long itemId,
                                                @Validated({Create.class}) @RequestBody CommentDto commentDto) {
        log.info("Controller = {}, UserId = {} ,Create comment = {}", this.getClass().getSimpleName(), userId,
                commentDto);
        return itemClient.createComment(userId, itemId, commentDto);
    }


    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> update(@RequestHeader("X-Sharer-User-Id") long userId,
                                         @PathVariable long itemId,
                                         @Validated({Update.class}) @RequestBody ItemDto itemDto) {
        log.info("Controller = {}, UserId = {} ,Update ItemId = {}, item = {}", this.getClass().getSimpleName(),
                userId, itemId, itemDto);
        return itemClient.update(userId, itemId, itemDto);
    }
}
