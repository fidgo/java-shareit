package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.shareit.Create;
import ru.practicum.shareit.request.ItemRequest;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * DTO предмета входной и выходной, при создании и изменении
 */
@Getter
@Setter
@AllArgsConstructor
@ToString
public class ItemDto {

    private Long id;
    @NotNull(groups = {Create.class}, message = "name не null")
    @NotBlank(groups = {Create.class}, message = "name не пустой")
    private String name;
    @NotNull(groups = {Create.class}, message = "description не null")
    @NotBlank(groups = {Create.class}, message = "description не пустой")
    private String description;
    @NotNull(groups = {Create.class}, message = "available не null")
    private Boolean available;
    private ItemRequest itemRequest;
}
