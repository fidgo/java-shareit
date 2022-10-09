package ru.practicum.shareit.item.interfaces;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto create(long userId, ItemDto itemDto);

    ItemDto update(long userId, long itemId, ItemDto itemDto);

    ItemDto get(long itemId);

    List<ItemDto> getAllItemsByUserID(long userId);

    List<ItemDto> search(String text);

}
