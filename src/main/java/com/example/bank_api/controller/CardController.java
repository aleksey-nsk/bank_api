package com.example.bank_api.controller;

import com.example.bank_api.dto.CardDto;
import com.example.bank_api.service.CardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/v1/client")
public class CardController {

    @Autowired
    private CardService cardService;

    @GetMapping("/{client_id}/account/{account_id}/card")
    public List<CardDto> findAll(
            @PathVariable("client_id") Long clientId,
            @PathVariable("account_id") Long accountId) {
        List<CardDto> cardDtoList = cardService.findAll(clientId, accountId);
        if (cardDtoList == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return cardDtoList;
    }
}
