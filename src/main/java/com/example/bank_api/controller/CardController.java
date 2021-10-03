package com.example.bank_api.controller;

import com.example.bank_api.dto.CardDto;
import com.example.bank_api.service.CardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/v1/client/{client_id}/account/{account_id}/card")
public class CardController {

    @Autowired
    private CardService cardService;

    @GetMapping
    public List<CardDto> findAll(
            @PathVariable("client_id") Long clientId,
            @PathVariable("account_id") Long accountId) {
        List<CardDto> cardDtoList = cardService.findAll(clientId, accountId);
        if (cardDtoList == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return cardDtoList;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CardDto save(
            @PathVariable("client_id") Long clientId,
            @PathVariable("account_id") Long accountId,
            @RequestBody CardDto cardDto) {
        CardDto saved = cardService.save(clientId, accountId, cardDto);
        if (saved == null) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY);
        }
        return saved;
    }
}
