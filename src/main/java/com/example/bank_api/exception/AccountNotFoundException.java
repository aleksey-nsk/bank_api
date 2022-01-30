package com.example.bank_api.exception;

public class AccountNotFoundException extends RuntimeException {

    public AccountNotFoundException(String message) {
        super(message);
    }
}
