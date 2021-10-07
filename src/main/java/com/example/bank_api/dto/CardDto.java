package com.example.bank_api.dto;

import com.example.bank_api.entity.Card;
import lombok.Data;

import java.util.Date;

@Data
public class CardDto {

    private Long id;
    private String number;
    private Date releaseDate;

    public CardDto(Long id, String number, Date releaseDate) {
        this.id = id;
        this.number = number;
        this.releaseDate = releaseDate;
    }

    public static CardDto valueOf(Card card) {
        return new CardDto(
                card.getId(),
                card.getNumber(),
                card.getReleaseDate()
        );
    }

    public Card mapToCard() {
        return new Card(id, number, releaseDate);
    }
}
