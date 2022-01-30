package com.example.bank_api.exception;

public class ClientDuplicateException extends RuntimeException {

    public ClientDuplicateException(String message) {
        super(message);
    }
}
