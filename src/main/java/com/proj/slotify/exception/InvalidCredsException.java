package com.proj.slotify.exception;

public class InvalidCredsException extends RuntimeException {
    public InvalidCredsException(String message) {
        super(message);
    }
}