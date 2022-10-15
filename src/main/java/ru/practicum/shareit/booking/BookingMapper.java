package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingUpdateDto;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;

import java.util.ArrayList;
import java.util.List;

public class BookingMapper {
    public static BookingDto toBookingDto(Booking booking) {
        BookingDto dto = new BookingDto();
        dto.setId(booking.getId());
        dto.setStart(booking.getStart());
        dto.setEnd(booking.getEnd());
        dto.setStatus(booking.getStatus());
        dto.setBooker(UserMapper.toUserBookigDto(booking.getBooker()));
        dto.setItem(ItemMapper.toItemBookingDto(booking.getItem()));
        return dto;
    }

    public static BookingUpdateDto toBookingUpdateDto(Booking booking) {
        BookingUpdateDto dto = new BookingUpdateDto();
        dto.setId(booking.getId());
        dto.setStatus(booking.getStatus());
        dto.setBooker(UserMapper.toUserBookigUpdateDto(booking.getBooker()));
        dto.setItem(ItemMapper.toItemBookingUpdateDto(booking.getItem()));
        dto.setStart(booking.getStart());
        dto.setEnd(booking.getEnd());
        return dto;
    }

    public static List<BookingUpdateDto> bookingUpdateDtoList(List<Booking> bookings) {
        List<BookingUpdateDto> dtos = new ArrayList<>();
        for (Booking booking : bookings) {
            dtos.add(toBookingUpdateDto(booking));
        }
        return dtos;
    }

    public static Booking toBooking(BookingDto bookingDto, User booker, Item item) {

        return new Booking(
                null,
                bookingDto.getStart(),
                bookingDto.getEnd(),
                item,
                booker,
                null
        );
    }
}
