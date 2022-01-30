package com.example.bank_api.service.impl;

import com.example.bank_api.dto.AccountDto;
import com.example.bank_api.entity.Account;
import com.example.bank_api.entity.Card;
import com.example.bank_api.entity.Client;
import com.example.bank_api.exception.AccountNotFoundException;
import com.example.bank_api.exception.CardNotFoundException;
import com.example.bank_api.exception.ClientNotFoundException;
import com.example.bank_api.repository.AccountRepository;
import com.example.bank_api.repository.ClientRepository;
import com.example.bank_api.service.AccountService;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Log4j2
public class AccountServiceImpl implements AccountService {

    private final ClientRepository clientRepository;
    private final AccountRepository accountRepository;

    @Autowired
    public AccountServiceImpl(ClientRepository clientRepository, AccountRepository accountRepository) {
        this.clientRepository = clientRepository;
        this.accountRepository = accountRepository;
    }

    @Override
    public List<AccountDto> findAll(Long clientId) {
        Client client = clientRepository.findById(clientId).orElseThrow(() -> new ClientNotFoundException(clientId));

        List<AccountDto> accountDtoList = client.getAccounts()
                .stream()
                .map(it -> AccountDto.valueOf(it))
                .collect(Collectors.toList());

        log.debug("Список всех счетов клиента: " + accountDtoList);
        return accountDtoList;
    }

    @Override
    public AccountDto findById(Long clientId, Long accountId) {
        Client client = clientRepository.findById(clientId).orElseThrow(() -> new ClientNotFoundException(clientId));

        AccountDto accountDto = client.getAccounts()
                .stream()
                .filter(it -> it.getId().equals(accountId))
                .map(it -> AccountDto.valueOf(it))
                .findFirst()
                .orElseThrow(() -> new AccountNotFoundException(accountId));

        // У клиента с id=111 отсутствует счёт с id=2222

        log.debug("По clientId=" + clientId + " и accountId=" + accountId + " получен счёт: " + accountDto);
        return accountDto;
    }

    @Override
    @Transactional
    public AccountDto save(Long clientId) {
        Client client = clientRepository.findById(clientId).orElseThrow(() -> new ClientNotFoundException(clientId));

        String number = RandomStringUtils.randomNumeric(20); // случайный 20-значный номер счёта
        Date openingDate = new Date(); // дата открытия счёта - текущая дата
        BigDecimal balance = BigDecimal.valueOf(0);
        List<Card> cards = Collections.emptyList();

        Account account = new Account(number, openingDate, balance, cards);

        AccountDto saved = AccountDto.valueOf(accountRepository.save(account)); // сохранить в БД счёт

        accountRepository.updateAccountSetClient(client, saved.getId()); // сохранённому счёту указать id клиента

        log.debug("Клиенту с id=" + clientId + " был добавлен счёт: " + saved);

        return saved;
    }

    @Override
    @Transactional
    public void updateAccountAddBalanceByCardNumber(Long clientId, String cardNumber, BigDecimal add) {
        log.debug("");
        log.debug("Внести деньги на счёт, по номеру карты");
        log.debug("  clientId: " + clientId);
        log.debug("  cardNumber: " + cardNumber);
        log.debug("  add: " + add);

        Client client = clientRepository.findById(clientId).orElseThrow(() -> new ClientNotFoundException(clientId));

        // flatMap решает проблему !!!!!!!!!!!!!!!!!!!
        List<Card> allCards = client.getAccounts()
                .stream()
                .flatMap(account -> account.getCards().stream())
                .collect(Collectors.toList());
        log.debug("Все карты клиента: " + allCards);

        Card card = allCards.stream()
                .filter(it -> it.getNumber().equalsIgnoreCase(cardNumber))
                .findFirst()
                .orElseThrow(() -> new CardNotFoundException(cardNumber));

        // У клиента с id=111 отсутствует карта с number=2222

        Account account = card.getAccount();

        BigDecimal currentBalance = account.getBalance();
        log.debug("");
        log.debug("Текущий баланс на счёте: " + currentBalance);

        BigDecimal newBalance = currentBalance.add(add);
        log.debug("Новый баланс на счёте: " + newBalance);

        log.debug("Обновить счёт - установить новый баланс");
        accountRepository.updateAccountSetBalance(account.getId(), newBalance);
    }
}
