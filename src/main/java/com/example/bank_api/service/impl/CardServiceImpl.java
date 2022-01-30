package com.example.bank_api.service.impl;

import com.example.bank_api.dto.CardDto;
import com.example.bank_api.entity.Account;
import com.example.bank_api.entity.Card;
import com.example.bank_api.entity.Client;
import com.example.bank_api.exception.AccountNotFoundException;
import com.example.bank_api.exception.CardNotFoundException;
import com.example.bank_api.exception.ClientNotFoundException;
import com.example.bank_api.repository.CardRepository;
import com.example.bank_api.repository.ClientRepository;
import com.example.bank_api.service.CardService;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Log4j2
public class CardServiceImpl implements CardService {

    private final ClientRepository clientRepository;
    private final CardRepository cardRepository;

    @Autowired
    public CardServiceImpl(ClientRepository clientRepository, CardRepository cardRepository) {
        this.clientRepository = clientRepository;
        this.cardRepository = cardRepository;
    }

    @Override
    @Transactional
    public CardDto save(Long clientId, Long accountId) {
        log.debug("");
        log.debug("Выпуск новой карты по счёту");

//        CardDto saved = null;
//
//        Account account = accountRepository.findAccountByIdAndClient_Id(accountId, clientId);
//        if (account != null) {
//            String number = RandomStringUtils.randomNumeric(16);
//            log.debug("");
//            log.debug("Сгенерирован случайный номер карты: " + number);
//
//            Date currentDate = new Date();
//            log.debug("Текущая дата: " + currentDate);
//
//            Card card = new Card(number, currentDate);
//            log.debug("Параметры для сохранения карты: " + card);
//
//            saved = CardDto.valueOf(cardRepository.save(card));
//            log.debug("В БД сохранена карта: " + saved);
//
//            log.debug("Для сохранённой карты указать идентификатор счёта");
//            cardRepository.updateCardSetAccount(account, saved.getId());
//        }
//
//        return saved;

        Client client = clientRepository.findById(clientId).orElseThrow(() -> new ClientNotFoundException(clientId));

        Account account = client.getAccounts()
                .stream()
                .filter(it -> it.getId().equals(accountId))
                .findFirst()
                .orElseThrow(() -> new AccountNotFoundException(accountId));
        log.debug("По clientId=" + clientId + " и accountId=" + accountId + " получен счёт: " + account);

        String number = RandomStringUtils.randomNumeric(16); // случайный 16-значный номер карты
        log.debug("");
        log.debug("Сгенерирован случайный номер карты: " + number);

        Date releaseDate = new Date(); // дата выпуска карты - текущая дата
        log.debug("Текущая дата: " + releaseDate);

        Card card = new Card(number, releaseDate);
        log.debug("Параметры для сохранения карты: " + card);

        CardDto saved = CardDto.valueOf(cardRepository.save(card)); // сохранить в БД карту
        log.debug("В БД сохранена карта: " + saved);

        log.debug("Для сохранённой карты указать идентификатор счёта");
        cardRepository.updateCardSetAccount(account, saved.getId()); // сохранённой карте указать id счёта

        return saved;
    }

    @Override
    public void delete(Long clientId, Long cardId) {
        log.debug("Удалить из БД карту");
        log.debug("clientId: " + clientId);
        log.debug("cardId: " + cardId);

        Client client = clientRepository.findById(clientId).orElseThrow(() -> new ClientNotFoundException(clientId));

        List<Card> finalListCards = new ArrayList<>();
        List<Account> accounts = client.getAccounts();
        for (Account a : accounts) {
            List<Card> cards = a.getCards();
            finalListCards.addAll(cards);
        }
        log.debug("Все карты клиента: " + finalListCards);

        Card card = finalListCards.stream()
                .filter(it -> it.getId().equals(cardId))
                .findFirst()
                .orElseThrow(() -> new CardNotFoundException(cardId));

        // У клиента с id=111 отсутствует карта с id=2222


        log.debug("Удалить карту: " + card);
        cardRepository.deleteById(cardId);
    }
}
