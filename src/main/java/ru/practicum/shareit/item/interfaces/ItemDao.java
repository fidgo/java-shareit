package ru.practicum.shareit.item.interfaces;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemDao {
    Item save(Item item);

    Item get(long itemId);

    Item update(Item item);

    List<Item> getByUserId(long userId);

    List<Item> getByText(String text);

}
