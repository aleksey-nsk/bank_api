package com.example.bank_api.exception;

public class CardNotFoundException extends RuntimeException {

    public CardNotFoundException(String cardNumber) {
        super("Не найдена карта с номером cardNumber=" + cardNumber);
    }

    public CardNotFoundException(Long cardId) {
        super("Не найдена карта с идентификатором cardId=" + cardId);
    }
}
