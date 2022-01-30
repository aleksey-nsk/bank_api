package com.example.bank_api.exception;

public class CardNotFoundException extends RuntimeException {

    public CardNotFoundException(String message) {
        super(message);
    }
}
