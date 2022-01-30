package com.example.bank_api.controller;

import com.example.bank_api.dto.CardDto;
import com.example.bank_api.service.CardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/client/{client_id}")
//@Api(description = "Контроллер для карт")
public class CardController {

    private final CardService cardService;

    @Autowired
    public CardController(CardService cardService) {
        this.cardService = cardService;
    }

    @PostMapping("/account/{account_id}/card")
    @ResponseStatus(HttpStatus.CREATED)
//    @ApiOperation(value = "Добавить карту по счёту")
    public CardDto save(@PathVariable("client_id") Long clientId, @PathVariable("account_id") Long accountId) {
//        CardDto saved = cardService.save(clientId, accountId);
//        if (saved == null) {
//            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY);
//        }
//        return saved;

        return cardService.save(clientId, accountId);
    }

    @DeleteMapping("/card/{card_id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
//    @ApiOperation(value = "Удалить карту")
    public void delete(@PathVariable("client_id") Long clientId, @PathVariable("card_id") Long cardId) {
        cardService.delete(clientId, cardId);
    }
}
