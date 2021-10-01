package com.example.bank_api.controller;

import com.example.bank_api.entity.Card;
import com.example.bank_api.service.CardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/card")
public class CardController {

    @Autowired
    private CardService cardService;

    @GetMapping
    public List<Card> findAll() {
        return cardService.findAll();
    }
}
