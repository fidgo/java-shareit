package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BookingUpdateDto {
    private Long id;
    private BookingState status;
    private UserBookingUpdateDto booker;
    private ItemBookingUpdateDto item;
    private LocalDateTime start;
    private LocalDateTime end;

    @Getter
    @Setter
    @AllArgsConstructor
    @ToString
    public static class ItemBookingUpdateDto {
        private Long id;
        private String name;
        private String description;
        private Boolean available;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @ToString
    public static class UserBookingUpdateDto {
        private Long id;
        private String name;
        private String email;
    }

}
