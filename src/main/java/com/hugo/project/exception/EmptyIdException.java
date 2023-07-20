package com.hugo.project.exception;

public class EmptyIdException extends IllegalArgumentException {
    public EmptyIdException(String message) {
        super(message);
    }
}
