package com.example.bank_api.service.impl;

import com.example.bank_api.dto.AccountDto;
import com.example.bank_api.dto.CardDto;
import com.example.bank_api.dto.ClientDto;
import com.example.bank_api.entity.Card;
import com.example.bank_api.repository.CardRepository;
import com.example.bank_api.service.AccountService;
import com.example.bank_api.service.CardService;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Log4j2
public class CardServiceImpl implements CardService {

    @Autowired
    private CardRepository cardRepository;

    @Autowired
    private AccountService accountService;

    @Override
    public List<CardDto> findAll(Long clientId, Long accountId) {
        log.debug("");
        log.debug("Поиск всех карт по идентификаторам");
        log.debug("clientId: " + clientId);
        log.debug("accountId: " + accountId);

        List<CardDto> cardDtoList = null;

        AccountDto accountDto = accountService.findById(clientId, accountId);
        if (accountDto != null) {
            cardDtoList = cardRepository.findAllByAccount_Id(accountId)
                    .stream()
                    .map(it -> CardDto.valueOf(it))
                    .collect(Collectors.toList());
        }

        log.debug("cardDtoList: " + cardDtoList);
        return cardDtoList;
    }

    @Override
    @Transactional
    public CardDto save(Long clientId, Long accountId, CardDto cardDto) {
        log.debug("");
        log.debug("Выпуск новой карты по счёту");
//        log.debug("clientId: " + clientId);
//        log.debug("accountId: " + accountId);
//        log.debug("cardDto: " + cardDto);

        AccountDto accountDto = accountService.findById(clientId, accountId);
        if (accountDto == null) {
            log.debug("Не найден клиент клиент или счёт");
            return null;
        }

//        Card card = cardDto.mapToCard();
//        log.debug("card: " + card);

//        String numberOld = cardDto.getNumber();
//        log.debug("numberOld: " + numberOld);

        String number = RandomStringUtils.randomNumeric(16);
        log.debug("");
        log.debug("Сгенерирован случайный номер карты: " + number);

//        Card cardByNumber = cardRepository.findCardByNumber(number);
//        if (cardByNumber != null) {
//            log.debug("Банк уже выпустил карту с номером: " + number);
//            return null;
//        }

        //"releaseDate": "2020-11-16T17:00:00.000+00:00"
        Date currentDate = new Date();
        log.debug("Текущая дата: " + currentDate);

        Card card = new Card();
        card.setNumber(number);
        card.setReleaseDate(currentDate);
//        card.setAccount(accountDto.mapToAccount());
        log.debug("Параметры для сохранения карты: " + card);

        CardDto saved = null;

//        Card saved1 = cardRepository.save(card);
//        log.debug("saved1: " + saved1);

        saved = CardDto.valueOf(cardRepository.save(card));
        log.debug("В БД сохранена карта: " + saved);

        log.debug("Для сохранённой карты указать идентификатор счёта");
        cardRepository.updateCardSetAccount(accountDto.mapToAccount(), saved.getId());

        return saved;
    }
}
