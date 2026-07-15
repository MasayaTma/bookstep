package com.example.bookstep.entity;

public enum BookStatus {
    UNREAD("未読"),
    READING("読書中"),
    COMPLETED("読了"),
    PAUSED("中断");

    private final String displayName;

    BookStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
