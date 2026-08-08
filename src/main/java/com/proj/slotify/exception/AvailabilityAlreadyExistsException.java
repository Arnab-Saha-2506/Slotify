package com.proj.slotify.exception;

public class AvailabilityAlreadyExistsException extends RuntimeException {
    public AvailabilityAlreadyExistsException(String message) {
        super(message);
    }
}