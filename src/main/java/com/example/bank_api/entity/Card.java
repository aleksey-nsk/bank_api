package com.example.bank_api.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "cards")
@Data
public class Card {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "number")
    private String number;

    @Column(name = "release_date")
    private Date releaseDate;

    @ManyToOne()
    @JoinColumn(name = "account_id")
    private Account account;

    public Card() {
    }

    public Card(Long id, String number, Date releaseDate) {
        this.id = id;
        this.number = number;
        this.releaseDate = releaseDate;
    }

    public Card(String number, Date releaseDate) {
        this.number = number;
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
                ", number='" + number + '\'' +
                ", releaseDate=" + releaseDate +
                '}';
    }
}
