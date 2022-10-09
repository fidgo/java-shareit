package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.error.InvalidAccessException;
import ru.practicum.shareit.error.NoSuchElemException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.interfaces.ItemDao;
import ru.practicum.shareit.item.interfaces.ItemService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.interfaces.UserDao;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final UserDao userDao;
    private final ItemDao itemDao;

    @Override
    public ItemDto create(long userId, ItemDto itemDto) {
        User owner = userDao.get(userId);
        if (owner == null) {
            throw new NoSuchElemException("Нет такого пользователя");
        }

        Item item = ItemMapper.toItem(owner, itemDto);
        return ItemMapper.toItemDto(itemDao.save(item));
    }

    @Override
    public ItemDto update(long userId, long itemId, ItemDto itemDto) {
        User owner = userDao.get(userId);
        if (owner == null) {
            throw new NoSuchElemException("Нет такого пользователя!");
        }

        Item item = itemDao.get(itemId);
        if (item == null) {
            throw new NoSuchElemException("Нет такого предмета!");
        }

        if (item.getOwner().getId() != userId) {
            throw new InvalidAccessException("Такой пользователь не может модифицировать предмет");
        }

        Item toUpdate = getToUpdate(item, itemDto);

        return ItemMapper.toItemDto(itemDao.update(toUpdate));
    }


    @Override
    public ItemDto get(long itemId) {
        Item item = itemDao.get(itemId);
        if (item == null) {
            throw new NoSuchElemException("Нет такого предмета!");
        }

        return ItemMapper.toItemDto(item);
    }

    @Override
    public List<ItemDto> getAllItemsByUserID(long userId) {
        User owner = userDao.get(userId);
        if (owner == null) {
            throw new NoSuchElemException("Нет такого пользователя!");
        }

        List<Item> itemsByUserId = itemDao.getByUserId(userId);
        return ItemMapper.toItemsDto(itemsByUserId);
    }

    @Override
    public List<ItemDto> search(String text) {
        text = text != null ? text : "";
        List<Item> items = itemDao.getByText(text);
        return ItemMapper.toItemsDto(items);
    }

    private Item getToUpdate(Item item, ItemDto itemDto) {
        return new Item(
                item.getId(),
                itemDto.getName() != null ? itemDto.getName() : item.getName(),
                itemDto.getDescription() != null ? itemDto.getDescription() : item.getDescription(),
                itemDto.getAvailable() != null ? itemDto.getAvailable() : item.getAvailable(),
                item.getOwner(),
                item.getItemRequest()
        );
    }
}
