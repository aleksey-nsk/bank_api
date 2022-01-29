//package com.example.bank_api.service.impl;
//
//import com.example.bank_api.dto.AccountDto;
//import com.example.bank_api.dto.ClientDto;
//import com.example.bank_api.entity.Account;
//import com.example.bank_api.entity.Card;
//import com.example.bank_api.entity.Client;
//import com.example.bank_api.repository.AccountRepository;
//import com.example.bank_api.repository.CardRepository;
//import com.example.bank_api.service.AccountService;
//import com.example.bank_api.service.ClientService;
//import lombok.extern.log4j.Log4j2;
//import org.apache.commons.lang.RandomStringUtils;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.math.BigDecimal;
//import java.util.Collections;
//import java.util.Date;
//import java.util.List;
//import java.util.stream.Collectors;
//
//@Service
//@Log4j2
//public class AccountServiceImpl implements AccountService {
//
//    @Autowired
//    private AccountRepository accountRepository;
//
//    @Autowired
//    private CardRepository cardRepository;
//
//    @Autowired
//    private ClientService clientService;
//
//    @Override
//    public List<AccountDto> findAll(Long clientId) {
//        List<AccountDto> accountDtoList = null;
//
//        ClientDto clientDto = clientService.findById(clientId);
//        if (clientDto != null) {
//            accountDtoList = accountRepository.findAccountByClientId(clientId)
//                    .stream()
//                    .map(it -> AccountDto.valueOf(it))
//                    .collect(Collectors.toList());
//        }
//
//        log.debug("Список всех счетов клиента: " + accountDtoList);
//        return accountDtoList;
//    }
//
//    @Override
//    @Transactional
//    public AccountDto save(Long clientId) {
//        log.debug("");
//        log.debug("Добавить счёт клиенту (без карт)");
//
//        AccountDto saved = null;
//
//        ClientDto clientDto = clientService.findById(clientId);
//        if (clientDto != null) {
//            String number = RandomStringUtils.randomNumeric(20);
//            log.debug("");
//            log.debug("Сгенерирован случайный номер счёта: " + number);
//
//            Date currentDate = new Date();
//            log.debug("Текущая дата: " + currentDate);
//
//            Account account = new Account(number, currentDate, BigDecimal.valueOf(0), Collections.emptyList());
//            log.debug("Параметры для сохранения счёта: " + account);
//
//            saved = AccountDto.valueOf(accountRepository.save(account));
//            log.debug("В БД сохранён счёт: " + saved);
//
//            log.debug("Для сохранённого счёта указать идентификатор клиента");
//            accountRepository.updateAccountSetClient(clientDto.mapToClient(), saved.getId());
//        }
//
//        return saved;
//    }
//
//    @Override
//    @Transactional
//    public boolean updateAccountAddBalanceByCardNumber(Long clientId, String cardNumber, BigDecimal add) {
//        log.debug("");
//        log.debug("Внести деньги на счёт, по номеру карты");
//        log.debug("  clientId: " + clientId);
//        log.debug("  cardNumber: " + cardNumber);
//        log.debug("  add: " + add);
//
//        boolean updated = false;
//
//        ClientDto clientDto = clientService.findById(clientId);
//        Card card = cardRepository.findCardByNumber(cardNumber);
//        log.debug("card: " + card);
//
//        if (clientDto != null && card != null) {
//            Account accountByCard = card.getAccount();
//            log.debug("accountByCard: " + accountByCard);
//
//            Client clientByAccount = accountByCard.getClient();
//            log.debug("clientByAccount: " + clientByAccount);
//
//            if (clientByAccount.getId().equals(clientId)) {
//                BigDecimal currentBalance = accountByCard.getBalance();
//                log.debug("");
//                log.debug("Текущий баланс на счёте: " + currentBalance);
//
//                BigDecimal newBalance = currentBalance.add(add);
//                log.debug("Новый баланс на счёте: " + newBalance);
//
//                log.debug("Обновить счёт - установить новый баланс");
//                accountRepository.updateAccountSetBalance(accountByCard.getId(), newBalance);
//                updated = true;
//            }
//        }
//
//        return updated;
//    }
//}
