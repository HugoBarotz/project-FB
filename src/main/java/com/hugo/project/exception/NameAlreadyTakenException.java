package com.hugo.project.exception;

public class NameAlreadyTakenException extends IllegalArgumentException {
    public NameAlreadyTakenException(String message) {
        super(message);
    }
}
