package com.example.bank_api.exception;

public class AccountNotFoundException extends RuntimeException {

    public AccountNotFoundException(Long id) {
        super("Не найден аккаунт по id=" + id);
    }
}
