package ru.practicum.shareit.error;

public class InvalidBookerExcception extends RuntimeException {
    public InvalidBookerExcception(String message) {
        super(message);
    }
}
