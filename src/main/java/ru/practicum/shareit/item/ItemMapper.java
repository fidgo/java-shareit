package ru.practicum.shareit.item;

import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemInfoDto;
import ru.practicum.shareit.item.model.Comment;
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
                owner
        );
    }

    public static ItemDto toItemDto(Item item) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable()
        );
    }

    public static List<ItemDto> toItemsDto(List<Item> itemsByUserId) {
        List<ItemDto> itemsDto = new ArrayList<>();
        for (Item item : itemsByUserId) {
            itemsDto.add(toItemDto(item));
        }
        return itemsDto;
    }

    public static ItemInfoDto toItemInfoDto(Item item, List<Booking> bookingsOfOwnerItem, List<Comment> commentsOfItem) {
        ItemInfoDto infoDto = new ItemInfoDto();
        infoDto.setId(item.getId());
        infoDto.setName(item.getName());
        infoDto.setDescription(item.getDescription());
        infoDto.setAvailable(item.getAvailable());

        List<CommentDto> commentDtos = new ArrayList<>();
        for (Comment comment : commentsOfItem) {
            commentDtos.add(toCommentDto(comment));
        }
        infoDto.setComments(commentDtos);

        if (bookingsOfOwnerItem == null || bookingsOfOwnerItem.size() != 2) {
            return infoDto;
        }

        Booking booking1 = bookingsOfOwnerItem.get(0);
        Booking booking2 = bookingsOfOwnerItem.get(1);

        ItemInfoDto.BookingInfoDto lastBooking = new ItemInfoDto.BookingInfoDto(booking1.getId(),
                booking1.getBooker().getId());
        ItemInfoDto.BookingInfoDto nextBooking = new ItemInfoDto.BookingInfoDto(booking2.getId(),
                booking2.getBooker().getId());

        infoDto.setLastBooking(lastBooking);
        infoDto.setNextBooking(nextBooking);

        return infoDto;
    }

    public static Comment toComment(CommentDto commentDto, User booker, Item item) {
        return new Comment(
                null,
                commentDto.getText(),
                booker,
                item,
                null
        );
    }

    public static CommentDto toCommentDto(Comment commentDB) {
        return new CommentDto(
                commentDB.getId(),
                commentDB.getText(),
                commentDB.getAuthor().getName(),
                commentDB.getCreated()
        );
    }
}
