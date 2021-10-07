package com.example.bank_api.controller;

import com.example.bank_api.dto.CardDto;
import com.example.bank_api.service.CardService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/v1/client/{client_id}")
@Api(description = "Контроллер для карт")
public class CardController {

    @Autowired
    private CardService cardService;

    @GetMapping("/card")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Получить все карты клиента")
    public List<CardDto> findAll(@PathVariable("client_id") Long clientId) {
        List<CardDto> cardDtoList = cardService.findAll(clientId);
        if (cardDtoList == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return cardDtoList;
    }

    @GetMapping("/account/{account_id}/card")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Получить все карты клиента по счёту")
    public List<CardDto> findAllByAccount(
            @PathVariable("client_id") Long clientId,
            @PathVariable("account_id") Long accountId) {
        List<CardDto> cardDtoList = cardService.findAllByAccount(clientId, accountId);
        if (cardDtoList == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return cardDtoList;
    }

    @PostMapping("/account/{account_id}/card")
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation(value = "Добавить карту по счёту")
    public CardDto save(
            @PathVariable("client_id") Long clientId,
            @PathVariable("account_id") Long accountId
            /*,@RequestBody CardDto cardDto*/) {
        CardDto saved = cardService.save(clientId, accountId);
        if (saved == null) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY);
        }
        return saved;
    }

    @DeleteMapping("/card/{card_id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ApiOperation(value = "Удалить карту")
    public void delete(
            @PathVariable("client_id") Long clientId,
            @PathVariable("card_id") Long cardId
    ) {
        cardService.delete(clientId, cardId);
    }
}
