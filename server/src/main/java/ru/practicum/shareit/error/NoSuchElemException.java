package ru.practicum.shareit.error;

public class NoSuchElemException extends RuntimeException {
    public NoSuchElemException(String message) {
        super(message);
    }
}
