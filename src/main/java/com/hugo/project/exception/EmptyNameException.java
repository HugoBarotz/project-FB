package com.hugo.project.exception;

public class EmptyNameException extends IllegalArgumentException {
    public EmptyNameException(String message) {
        super(message);
    }
}
