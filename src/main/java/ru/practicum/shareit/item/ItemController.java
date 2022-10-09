package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.Create;
import ru.practicum.shareit.Update;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.interfaces.ItemService;

import java.util.List;

/**
 * Контроллер для управления предметами(items) — создания, редактирования, удаления, поиска и просмотра
 * предметов пользователей.
 */
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {
    private final ItemService itemService;

    @GetMapping
    List<ItemDto> getAll(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Controller = {}, UserId = {} , get all Items", this.getClass().getSimpleName(), userId);
        List<ItemDto> dto = itemService.getAllItemsByUserID(userId);
        return dto;
    }

    @GetMapping("/{itemId}")
    ItemDto get(@RequestHeader("X-Sharer-User-Id") long userId,
                @PathVariable long itemId) {
        log.info("Controller = {}, UserId = {} ,get ItemId = {}", this.getClass().getSimpleName(), userId, itemId);
        ItemDto dto = itemService.get(itemId);
        return dto;
    }

    @GetMapping("/search")
    List<ItemDto> search(@RequestHeader("X-Sharer-User-Id") long userId,
                         @RequestParam(required = false) String text) {
        log.info("Controller = {}, UserId = {} ,get ItemId with text {}", this.getClass().getSimpleName(), userId, text);
        List<ItemDto> dtos = itemService.search(text);
        return dtos;
    }

    @PostMapping
    ItemDto create(@RequestHeader("X-Sharer-User-Id") long userId,
                   @Validated({Create.class}) @RequestBody ItemDto itemDto) {
        log.info("Controller = {}, UserId = {} ,Create Item = {}", this.getClass().getSimpleName(), userId, itemDto);
        ItemDto dto = itemService.create(userId, itemDto);
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
