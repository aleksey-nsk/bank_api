package com.example.bank_api.exception;

public class ClientNotFoundException extends RuntimeException {

    public ClientNotFoundException(Long id) {
        super("Не найден клиент по id=" + id);
    }
}
