package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.util.ArrayList;
import java.util.List;

public class ItemMapper {
    public static Item toItem(User owner, ItemDto itemDto) {

        return new Item(
                0L,
                itemDto.getName(),
                itemDto.getDescription(),
                true,
                owner,
                itemDto.getItemRequest() != null ? itemDto.getItemRequest() : null
        );
    }

    public static ItemDto toItemDto(Item item) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getItemRequest() != null ? item.getItemRequest() : null
        );

    }

    public static List<ItemDto> toItemsDto(List<Item> itemsByUserId) {
        List<ItemDto> itemsDto = new ArrayList<>();
        for (Item item : itemsByUserId) {
            itemsDto.add(toItemDto(item));
        }
        return itemsDto;
    }
}
