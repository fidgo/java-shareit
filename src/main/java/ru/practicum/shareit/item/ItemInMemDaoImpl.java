package ru.practicum.shareit.item;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.interfaces.ItemDao;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Repository
public class ItemInMemDaoImpl implements ItemDao {
    private final HashMap<Long, Item> items = new HashMap<>();
    private long idGenerator = 0L;

    @Override
    public Item save(Item item) {
        Long newIdItem = ++idGenerator;
        item.setId(newIdItem);
        items.put(newIdItem, item);

        return item;
    }

    @Override
    public Item get(long itemId) {
        Item item = null;
        if (items.containsKey(itemId)) {
            item = items.get(itemId);
        }
        return item;
    }

    @Override
    public Item update(Item item) {
        Long idItem = item.getId();
        items.remove(idItem);
        items.put(idItem, item);

        return item;
    }

    @Override
    public List<Item> getByUserId(long userId) {
        List<Item> itemsById = new ArrayList<>();

        for (Item item : items.values()) {
            if (item.getOwner().getId() == userId) {
                itemsById.add(item);
            }
        }

        return itemsById;
    }

    @Override
    public List<Item> getByText(String text) {
        List<Item> itemsWithText = new ArrayList<>();

        if ("".equals(text)) {
            return itemsWithText;
        }
        for (Item item : items.values()) {

            boolean isAvailable = item.getAvailable();
            boolean nameContainTextNoCase = item.getName().toLowerCase().contains(text.toLowerCase());
            boolean descriptionContainTextNoCase = item.getDescription().toLowerCase().contains(text.toLowerCase());

            if ((isAvailable) && ((nameContainTextNoCase) || (descriptionContainTextNoCase))) {
                itemsWithText.add(item);
            }
        }
        return itemsWithText;
    }
}
