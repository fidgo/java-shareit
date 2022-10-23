package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.interfaces.BookingRepository;
import ru.practicum.shareit.error.InvalidAccessException;
import ru.practicum.shareit.error.NoSuchElemException;
import ru.practicum.shareit.error.StatusElemException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemInfoDto;
import ru.practicum.shareit.item.interfaces.CommentRepository;
import ru.practicum.shareit.item.interfaces.ItemRepository;
import ru.practicum.shareit.item.interfaces.ItemService;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.interfaces.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Override
    @Transactional
    public ItemDto create(long userId, ItemDto itemDto) {

        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElemException("Нет такого пользователя"));

        Long requestId = itemDto.getRequestId();
        User requestor = null;
        if (itemDto.getRequestId() != null) {
            requestor = userRepository.findById(requestId)
                    .orElseThrow(() -> new NoSuchElemException("Нет такого пользователя"));
        }

        Item item = ItemMapper.toItem(owner, itemDto, requestor);
        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    @Transactional
    public ItemDto update(long userId, long itemId, ItemDto itemDto) {
        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElemException("Нет такого пользователя"));
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NoSuchElemException("Нет такого предмета"));
        if (item.getOwner().getId() != userId) {
            throw new InvalidAccessException("Такой пользователь не может модифицировать предмет");
        }
        Item toUpdate = getToUpdate(item, itemDto);

        return ItemMapper.toItemDto(itemRepository.save(toUpdate));
    }


    @Override
    @Transactional
    public ItemInfoDto get(long userId, long itemId) {
        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElemException("Нет такого пользователя"));
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NoSuchElemException("Нет такого предмета"));

        List<Booking> bookingsOfOwnerItem =
                bookingRepository.findTop2ByItem_Owner_IdAndItem_IdOrderByStartAsc(userId, itemId);
        List<Comment> commentsOfItem = commentRepository.findAllByItem_Id(itemId);

        return ItemMapper.toItemInfoDto(item, bookingsOfOwnerItem, commentsOfItem);
    }

    @Override
    @Transactional
    public List<ItemInfoDto> getAllItemsByUserID(long userId, PageRequest pageRequest) {

        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElemException("Нет такого пользователя"));

        Page<Item> itemPage = itemRepository.findAllByOwner(owner, pageRequest);
        List<Item> itemsByUserId = itemPage.stream().collect(Collectors.toList());
        List<ItemInfoDto> itemsInfoDtos = evaluateItemsInfoDtos(userId, itemsByUserId);

        return itemsInfoDtos;
    }


    @Override
    @Transactional
    public List<ItemDto> search(String text, PageRequest pageRequest) {

        if ("".equals(text)) {
            return new ArrayList<>();
        }
        Page<Item> itemPage = itemRepository.search(text, pageRequest);
        List<Item> items = itemPage.stream().collect(Collectors.toList());
        return ItemMapper.toItemsDto(items);

    }

    @Override
    public CommentDto createComment(long userId, long itemId, CommentDto commentDto) {
        User booker = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElemException("Нет такого пользователя"));

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NoSuchElemException("Нет такого предмета"));

        bookingRepository
                .findFirstByBooker_IdAndItem_IdAndEndBefore(userId, itemId, LocalDateTime.now())
                .orElseThrow(() -> new StatusElemException("Отзыв может оставить только тот пользователь, " +
                        "который брал эту вещь в аренду, и только после окончания срока аренды."));

        Comment commentDB = commentRepository.save(ItemMapper.toComment(commentDto, booker, item));

        return ItemMapper.toCommentDto(commentDB);
    }

    private List<ItemInfoDto> evaluateItemsInfoDtos(long userId, List<Item> itemsByUserId) {
        List<ItemInfoDto> dtos = new ArrayList<>();

        for (Item item : itemsByUserId) {
            List<Booking> bookingsOfOwnerItem =
                    bookingRepository.findTop2ByItem_Owner_IdAndItem_IdOrderByStartAsc(userId, item.getId());
            dtos.add(ItemMapper.toItemInfoDto(item, bookingsOfOwnerItem, new ArrayList<>()));
        }

        return dtos;
    }

    private Item getToUpdate(Item item, ItemDto itemDto) {
        return new Item(
                item.getId(),
                itemDto.getName() != null ? itemDto.getName() : item.getName(),
                itemDto.getDescription() != null ? itemDto.getDescription() : item.getDescription(),
                itemDto.getAvailable() != null ? itemDto.getAvailable() : item.getAvailable(),
                item.getOwner(),
                item.getRequest()
        );
    }
}
