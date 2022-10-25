package ru.practicum.shareit.item.interfaces;

import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemInfoDto;

import java.util.List;

public interface ItemService {
    ItemDto create(long userId, ItemDto itemDto);

    ItemDto update(long userId, long itemId, ItemDto itemDto);

    ItemInfoDto get(long userId, long itemId);

    List<ItemInfoDto> getAllItemsByUserID(long userId, PageRequest pageRequest);

    List<ItemDto> search(String text, PageRequest pageRequest);

    CommentDto createComment(long userId, long itemId, CommentDto commentDto);

}
