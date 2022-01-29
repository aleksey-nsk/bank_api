//package com.example.bank_api.dto;
//
//import com.example.bank_api.entity.Account;
//import com.example.bank_api.entity.Card;
//import com.example.bank_api.entity.Client;
//import lombok.Data;
//
//import java.math.BigDecimal;
//import java.util.Date;
//import java.util.List;
//
//@Data
//public class AccountDto {
//
//    private Long id;
//    private String number;
//    private Date openingDate;
//    private BigDecimal balance;
//    private List<Card> cards;
//
//    public AccountDto(Long id, String number, Date openingDate, BigDecimal balance, List<Card> cards) {
//        this.id = id;
//        this.number = number;
//        this.openingDate = openingDate;
//        this.balance = balance;
//        this.cards = cards;
//    }
//
//    public static AccountDto valueOf(Account account) {
//        return new AccountDto(
//                account.getId(),
//                account.getNumber(),
//                account.getOpeningDate(),
//                account.getBalance(),
//                account.getCards()
//        );
//    }
//
//    public Account mapToAccount() {
//        return new Account(id, number, openingDate, balance, cards);
//    }
//
//    @Override
//    public String toString() {
//        return "AccountDto{" +
//                "id=" + id +
//                ", number='" + number + '\'' +
//                ", openingDate=" + openingDate +
//                ", balance=" + balance +
//                ", cards=" + cards +
//                '}';
//    }
//}
