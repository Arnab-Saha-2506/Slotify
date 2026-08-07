package com.proj.slotify.exception;

public class SlotAlreadyBookedException extends RuntimeException{
    public SlotAlreadyBookedException(String message){
        super(message);
    }
}
