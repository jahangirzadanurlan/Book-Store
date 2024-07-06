package com.example.userms.model.enums;

public enum Constant {
    SAVE_IS_SUCCESSFULLY("Save is successfully\n " +
            "Confirmation link has been sent to you by email");

    private final String message;

    Constant(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
