package com.example.bank_api.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "cards")
@Data
@NoArgsConstructor
public class Card {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "card_number")
    private String cardNumber;

    @Column(name = "release_date")
    private Date releaseDate;

    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;

    public Card(Long id, String cardNumber, Date releaseDate) {
        this.id = id;
        this.cardNumber = cardNumber;
        this.releaseDate = releaseDate;
    }

    public Card(String cardNumber, Date releaseDate) {
        this.cardNumber = cardNumber;
        this.releaseDate = releaseDate;
    }

    @JsonIgnore
    public Account getAccount() {
        return account;
    }

    @Override
    public String toString() {
        return "Card{" +
                "id=" + id +
                ", cardNumber='" + cardNumber + '\'' +
                ", releaseDate=" + releaseDate +
                '}';
    }
}
