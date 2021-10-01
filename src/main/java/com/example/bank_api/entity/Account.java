package com.example.bank_api.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "accounts")
@Data
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "number")
    private String number;

    @Column(name = "opening_date")
    private Date openingDate;

    @Column(name = "money")
    private BigDecimal money;

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL)
    private List<Card> cards;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "client_id")
    private Client client;

    @JsonIgnore
    public Client getClient() {
        return client;
    }

    public Account() {
    }

    public Account(Long id, String number, Date openingDate, BigDecimal money, List<Card> cards) {
        this.id = id;
        this.number = number;
        this.openingDate = openingDate;
        this.money = money;
        this.cards = cards;
    }

    @Override
    public String toString() {
        return "Account{" +
                "id=" + id +
                ", number='" + number + '\'' +
                ", openingDate=" + openingDate +
                ", money=" + money +
                ", cards=" + cards +
                '}';
    }
}
