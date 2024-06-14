package ru.practicum.shareit.exceptions;

public class EmailDublicateException extends RuntimeException {
    public EmailDublicateException(String message) {
        super(message);
    }
}
