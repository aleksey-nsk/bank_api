package com.example.bank_api.service.impl;

import com.example.bank_api.entity.Card;
//import com.example.bank_api.repository.CardRepository;
import com.example.bank_api.repository.CardRepository;
import com.example.bank_api.service.CardService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@Log4j2
public class CardServiceImpl implements CardService {

    @Autowired
    private CardRepository cardRepository;

    @Override
    public List<Card> findAll() {
        List<Card> cardList = cardRepository.findAll();
        log.debug("Список всех карточек: " + cardList);
        return cardList;
    }
}
