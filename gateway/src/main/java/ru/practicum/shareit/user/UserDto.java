package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import ru.practicum.shareit.Create;
import ru.practicum.shareit.Update;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@AllArgsConstructor
@Getter
@ToString
public class UserDto {
    private Long id;
    @NotNull(groups = {Create.class}, message = "name не должен быть null")
    @NotBlank(groups = {Create.class}, message = "name не должен быть пустым")
    private String name;
    @NotNull(groups = {Create.class}, message = "Email не должен быть null")
    @Email(groups = {Create.class, Update.class}, message = "Email должен быть корректным адресом электронной почты")
    private String email;
}
