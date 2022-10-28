package ru.practicum.shareit.error;

public class AlreadySetStatusException extends RuntimeException {
    public AlreadySetStatusException(String message) {
        super(message);
    }
}
