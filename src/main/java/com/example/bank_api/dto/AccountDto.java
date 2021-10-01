package com.example.bank_api.dto;

import com.example.bank_api.entity.Account;
import com.example.bank_api.entity.Card;
import com.example.bank_api.entity.Client;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
public class AccountDto {

    private Long id;
    private String number;
    private Date openingDate;
    private BigDecimal money;
    private List<Card> cards;

    public AccountDto(Long id, String number, Date openingDate, BigDecimal money, List<Card> cards) {
        this.id = id;
        this.number = number;
        this.openingDate = openingDate;
        this.money = money;
        this.cards = cards;
    }

    public static AccountDto valueOf(Account account) {
        return new AccountDto(
                account.getId(),
                account.getNumber(),
                account.getOpeningDate(),
                account.getMoney(),
                account.getCards()
        );
    }

    public Account mapToAccount() {
        return new Account(id, number, openingDate, money, cards);
    }

    @Override
    public String toString() {
        return "AccountDto{" +
                "id=" + id +
                ", number='" + number + '\'' +
                ", openingDate=" + openingDate +
                ", money=" + money +
                ", cards=" + cards +
                '}';
    }
}
