package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.Create;
import ru.practicum.shareit.PageRequestFrom;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponsesDto;
import ru.practicum.shareit.request.interfaces.ItemRequestService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @GetMapping
    List<ItemRequestResponsesDto> getOwnRequests(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Controller = {}, UserId = {} , get own requests", this.getClass().getSimpleName(), userId);
        List<ItemRequestResponsesDto> dto = itemRequestService.getOwn(userId);
        return dto;
    }

    @GetMapping("/{requestId}")
    ItemRequestResponsesDto getRequest(@RequestHeader("X-Sharer-User-Id") long userId,
                                       @PathVariable long requestId) {
        log.info("Controller = {}, UserId = {} , get request by id = {}", this.getClass().getSimpleName(),
                userId, requestId);
        ItemRequestResponsesDto dto = itemRequestService.getById(userId, requestId);
        return dto;
    }

    @GetMapping("/all")
    List<ItemRequestResponsesDto> getAllRequests(@RequestHeader("X-Sharer-User-Id") long userId,
                                                 @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                                 @Positive @RequestParam(defaultValue = "10") Integer size) {
        log.info("Controller = {}, UserId = {} , get all requests from = {} size = {}", this.getClass().getSimpleName(),
                userId, from, size);
        PageRequest pageRequest = new PageRequestFrom(size, from, Sort.by("created").descending());
        List<ItemRequestResponsesDto> dto = itemRequestService.getAll(userId, pageRequest);
        return dto;
    }

    @PostMapping
    ItemRequestDto create(@RequestHeader("X-Sharer-User-Id") long userId,
                          @Validated({Create.class}) @RequestBody ItemRequestDto itemRequestDto) {
        log.info("Controller = {}, UserId = {} ,Create ItemRequestDto = {}", this.getClass().getSimpleName(),
                userId, itemRequestDto);
        ItemRequestDto dto = itemRequestService.create(userId, itemRequestDto);
        return dto;
    }

}
