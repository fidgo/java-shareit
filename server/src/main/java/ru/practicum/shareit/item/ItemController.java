package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.Create;
import ru.practicum.shareit.PageRequestFrom;
import ru.practicum.shareit.Update;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemInfoDto;
import ru.practicum.shareit.item.interfaces.ItemService;

import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {
    private final ItemService itemService;

    @GetMapping
    List<ItemInfoDto> getAll(@RequestHeader("X-Sharer-User-Id") long userId,
                             @RequestParam(defaultValue = "0") Integer from,
                             @RequestParam(defaultValue = "10") Integer size
    ) {
        log.info("Controller = {}, UserId = {} , get all Items from = {} and size = {}",
                this.getClass().getSimpleName(), userId, from, size);

        PageRequest pageRequest = new PageRequestFrom(size, from, Sort.unsorted());
        List<ItemInfoDto> dto = itemService.getAllItemsByUserID(userId, pageRequest);
        return dto;
    }

    @GetMapping("/{itemId}")
    ItemInfoDto get(@RequestHeader("X-Sharer-User-Id") long userId,
                    @PathVariable long itemId) {
        log.info("Controller = {}, UserId = {} ,get ItemId = {}", this.getClass().getSimpleName(), userId, itemId);
        ItemInfoDto dto = itemService.get(userId, itemId);
        return dto;
    }

    @GetMapping("/search")
    List<ItemDto> search(@RequestHeader("X-Sharer-User-Id") long userId,
                         @RequestParam(defaultValue = "0") Integer from,
                         @RequestParam(defaultValue = "10") Integer size,
                         @RequestParam(required = false) String text) {
        log.info("Controller = {}, UserId = {} ,get ItemId with text {} from = {} and size = {}",
                this.getClass().getSimpleName(), userId, text, from, size);

        PageRequest pageRequest = new PageRequestFrom(size, from, Sort.unsorted());
        List<ItemDto> dtos = itemService.search(text, pageRequest);
        return dtos;
    }

    @PostMapping
    ItemDto create(@RequestHeader("X-Sharer-User-Id") long userId,
                   @Validated({Create.class}) @RequestBody ItemDto itemDto) {
        log.info("Controller = {}, UserId = {} ,Create Item = {}", this.getClass().getSimpleName(), userId, itemDto);
        ItemDto dto = itemService.create(userId, itemDto);
        return dto;
    }

    @PostMapping("/{itemId}/comment")
    CommentDto createComment(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long itemId,
                             @Validated({Create.class}) @RequestBody CommentDto commentDto) {
        log.info("Controller = {}, UserId = {} ,Create comment = {}", this.getClass().getSimpleName(), userId,
                commentDto);
        CommentDto dto = itemService.createComment(userId, itemId, commentDto);
        return dto;
    }


    @PatchMapping("/{itemId}")
    ItemDto update(@RequestHeader("X-Sharer-User-Id") long userId,
                   @PathVariable long itemId,
                   @Validated({Update.class}) @RequestBody ItemDto itemDto) {
        log.info("Controller = {}, UserId = {} ,Update ItemId = {}, item = {}", this.getClass().getSimpleName(),
                userId, itemId, itemDto);
        ItemDto dto = itemService.update(userId, itemId, itemDto);
        return dto;
    }
}
