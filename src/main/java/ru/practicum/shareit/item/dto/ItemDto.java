package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.shareit.Create;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

/**
 * DTO предмета входной и выходной, при создании и изменении
 */
@Getter
@Setter
@AllArgsConstructor
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
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
    @Positive(groups = {Create.class}, message = "requestId не нуль или меньше")
    private Long requestId;
}
