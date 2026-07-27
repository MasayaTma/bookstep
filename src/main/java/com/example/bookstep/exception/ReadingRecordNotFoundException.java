package com.example.bookstep.exception;

public class ReadingRecordNotFoundException extends RuntimeException {
    public ReadingRecordNotFoundException(Long id) {
        super("Reading record not found: " + id);
    }
}
