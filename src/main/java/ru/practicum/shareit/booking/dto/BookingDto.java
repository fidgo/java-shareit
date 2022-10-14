package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import ru.practicum.shareit.Create;
import ru.practicum.shareit.booking.BookingState;
import ru.practicum.shareit.booking.CreateOutput;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@StartBeforeEndValidation(groups = {Create.class})
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BookingDto {
    @NotNull(groups = {CreateOutput.class})
    private Long id;
    @NotNull(groups = {Create.class})
    private Long itemId;
    @FutureOrPresent(groups = {Create.class})
    private LocalDateTime start;
    @Future(groups = {Create.class})
    private LocalDateTime end;
    private BookingState status;
    private User booker;
    private Item item;
}
