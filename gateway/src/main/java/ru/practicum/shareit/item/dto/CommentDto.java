package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.shareit.Create;

import javax.validation.constraints.NotEmpty;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@ToString
public class CommentDto {

    private Long id;

    @NotEmpty(groups = {Create.class})
    private String text;

    private String authorName;

    private LocalDateTime created;
}
