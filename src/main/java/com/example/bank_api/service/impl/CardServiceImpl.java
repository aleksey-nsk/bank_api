//package com.example.bank_api.service.impl;
//
//import com.example.bank_api.dto.CardDto;
//import com.example.bank_api.entity.Account;
//import com.example.bank_api.entity.Card;
//import com.example.bank_api.repository.AccountRepository;
//import com.example.bank_api.repository.CardRepository;
//import com.example.bank_api.repository.ClientRepository;
//import com.example.bank_api.service.CardService;
//import lombok.extern.log4j.Log4j2;
//import org.apache.commons.lang.RandomStringUtils;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.Date;
//
//@Service
//@Log4j2
//public class CardServiceImpl implements CardService {
//
//    @Autowired
//    private AccountRepository accountRepository;
//
//    @Autowired
//    private CardRepository cardRepository;
//
//    @Autowired
//    private ClientRepository clientRepository;
//
//    @Override
//    @Transactional
//    public CardDto save(Long clientId, Long accountId) {
//        log.debug("");
//        log.debug("Выпуск новой карты по счёту");
//
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
//    }
//
//    @Override
//    public void delete(Long clientId, Long cardId) {
//        log.debug("Удалить из БД карту");
//        log.debug("clientId: " + clientId);
//        log.debug("cardId: " + cardId);
//
//        if (clientRepository.findById(clientId).isPresent()) {
//            cardRepository.deleteById(cardId);
//        } else {
//            log.debug("В БД отсутствует клиент с id=" + clientId);
//        }
//    }
//}
