package com.example.bank_api.dto;

import com.example.bank_api.entity.Card;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CardDto {

    private Long id;
    private String cardNumber;
    private Date releaseDate;

    public static CardDto valueOf(Card card) {
        return new CardDto(
                card.getId(),
                card.getCardNumber(),
                card.getReleaseDate()
        );
    }

    public Card mapToCard() {
        return new Card(id, cardNumber, releaseDate);
    }
}
