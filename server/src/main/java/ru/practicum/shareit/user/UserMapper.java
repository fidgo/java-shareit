package ru.practicum.shareit.user;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingUpdateDto;

import java.util.ArrayList;
import java.util.List;

public class UserMapper {
    public static UserDto toUserDto(User user) {
        return new UserDto(
                user.getId(),
                user.getName(),
                user.getEmail()
        );
    }

    public static List<UserDto> toUsersDto(List<User> users) {
        List<UserDto> usersDto = new ArrayList<>();
        for (User user : users) {
            usersDto.add(toUserDto(user));
        }
        return usersDto;
    }

    public static User toUser(UserDto userDto) {
        return new User(
                userDto.getId(),
                userDto.getName(),
                userDto.getEmail()
        );
    }

    public static BookingDto.UserBookingDto toUserBookigDto(User booker) {
        return new BookingDto.UserBookingDto(
                booker.getId(),
                booker.getName(),
                booker.getEmail()
        );
    }

    public static BookingUpdateDto.UserBookingUpdateDto toUserBookigUpdateDto(User booker) {
        return new BookingUpdateDto.UserBookingUpdateDto(
                booker.getId(),
                booker.getName(),
                booker.getEmail()
        );
    }
}
