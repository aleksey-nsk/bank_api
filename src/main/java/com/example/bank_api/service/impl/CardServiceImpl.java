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

import java.util.Date;

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
        Client client = clientRepository.findById(clientId).orElseThrow(() -> new ClientNotFoundException(clientId));

        Account theAccount = client.getAccounts()
                .stream()
                .filter(account -> account.getId().equals(accountId))
                .findFirst()
                .orElseThrow(() -> new AccountNotFoundException("У клиента с id=" + clientId + " отсутствует счёт с id=" + accountId));

        String cardNumber = RandomStringUtils.randomNumeric(16); // случайный 16-значный номер карты
        Date releaseDate = new Date(); // дата выпуска карты - текущая дата

        Card card = new Card(cardNumber, releaseDate);
        CardDto saved = CardDto.valueOf(cardRepository.save(card)); // сохранить в БД карту
        cardRepository.updateCardSetAccount(theAccount, saved.getId()); // сохранённую карту привязать к счёту
        log.debug("Клиенту с id=" + clientId + " к счёту с id=" + accountId + " была привязана карта: " + saved);

        return saved;
    }

    @Override
    public void delete(Long clientId, Long cardId) {
        Client client = clientRepository.findById(clientId).orElseThrow(() -> new ClientNotFoundException(clientId));

        Card theCard = client.getAccounts()
                .stream()
                .flatMap(account -> account.getCards().stream())
                .filter(card -> card.getId().equals(cardId))
                .findFirst()
                .orElseThrow(() -> new CardNotFoundException("У клиента с id=" + clientId + " отсутствует карта с id=" + cardId));

        log.debug("У клиента с id=" + clientId + " удалить карту: " + theCard);
        cardRepository.deleteById(theCard.getId());
    }
}
