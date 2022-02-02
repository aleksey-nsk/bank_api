package com.example.bank_api.dto;

import com.example.bank_api.entity.Account;
import com.example.bank_api.entity.Card;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountDto {

    private Long id;
    private String number;
    private Date openingDate;
    private BigDecimal balance;
    private List<Card> cards;

    public static AccountDto valueOf(Account account) {
        return new AccountDto(
                account.getId(),
                account.getNumber(),
                account.getOpeningDate(),
                account.getBalance(),
                account.getCards()
        );
    }

    public Account mapToAccount() {
        return new Account(id, number, openingDate, balance, cards);
    }
}
