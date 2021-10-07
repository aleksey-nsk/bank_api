package com.example.bank_api.service.impl;

import com.example.bank_api.dto.AccountDto;
import com.example.bank_api.dto.CardDto;
import com.example.bank_api.entity.Card;
import com.example.bank_api.repository.CardRepository;
import com.example.bank_api.repository.ClientRepository;
import com.example.bank_api.service.AccountService;
import com.example.bank_api.service.CardService;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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

    @Autowired
    private ClientRepository clientRepository;

    @Override
    public List<CardDto> findAll(Long clientId) {
        log.debug("");
        log.debug("Поиск всех карт клиента");
        log.debug("clientId: " + clientId);

        List<Card> cardList = new ArrayList<>();

        List<AccountDto> accountDtoList = accountService.findAll(clientId);
        for (AccountDto accountDto : accountDtoList) {
            Long id = accountDto.mapToAccount().getId();
            List<Card> cardListByAccount = cardRepository.findAllByAccount_Id(id);
            cardList.addAll(cardListByAccount);
        }

        log.debug("cardList: " + cardList);

        List<CardDto> cardDtoList = cardList.stream()
                .map(it -> CardDto.valueOf(it))
                .collect(Collectors.toList());

        log.debug("cardDtoList: " + cardDtoList);
        return cardDtoList;
    }

    @Override
    public List<CardDto> findAllByAccount(Long clientId, Long accountId) {
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

        CardDto saved = null;

        AccountDto accountDto = accountService.findById(clientId, accountId);
        if (accountDto != null) {
            String number = RandomStringUtils.randomNumeric(16);
            log.debug("");
            log.debug("Сгенерирован случайный номер карты: " + number);

            Date currentDate = new Date();
            log.debug("Текущая дата: " + currentDate);

            Card card = new Card();
            card.setNumber(number);
            card.setReleaseDate(currentDate);
            log.debug("Параметры для сохранения карты: " + card);

            saved = CardDto.valueOf(cardRepository.save(card));
            log.debug("В БД сохранена карта: " + saved);

            log.debug("Для сохранённой карты указать идентификатор счёта");
            cardRepository.updateCardSetAccount(accountDto.mapToAccount(), saved.getId());
        }

        return saved;
    }

    @Override
    public void delete(Long clientId, Long cardId) {
        log.debug("Удалить из БД карту");
        log.debug("clientId: " + clientId);
        log.debug("cardId: " + cardId);

        if (clientRepository.findById(clientId).isPresent()) {
            cardRepository.deleteById(cardId);
        } else {
            log.debug("В БД отсутствует клиент с id=" + clientId);
        }
    }
}
