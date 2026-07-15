package com.example.bookstep.exception;

public class BookNotFoundException extends RuntimeException {

    public BookNotFoundException(Long bookId) {
        super("書籍が見つかりませんでした: " + bookId);
    }
}
