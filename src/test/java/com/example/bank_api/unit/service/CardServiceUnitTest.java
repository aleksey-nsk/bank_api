package com.example.bank_api.unit.service;

import com.example.bank_api.entity.Account;
import com.example.bank_api.entity.Card;
import com.example.bank_api.entity.Client;
import com.example.bank_api.exception.AccountNotFoundException;
import com.example.bank_api.exception.CardNotFoundException;
import com.example.bank_api.exception.ClientNotFoundException;
import com.example.bank_api.repository.CardRepository;
import com.example.bank_api.repository.ClientRepository;
import com.example.bank_api.service.CardService;
import com.example.bank_api.service.impl.CardServiceImpl;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang.RandomStringUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = CardServiceImpl.class)
@Log4j2
@ActiveProfiles("test")
public class CardServiceUnitTest {

    @Autowired
    private CardService cardService;

    @MockBean
    private ClientRepository clientRepository;

    @MockBean
    private CardRepository cardRepository;

    private Client createClient(Long id) {
        String last = RandomStringUtils.randomAlphabetic(10);
        String first = RandomStringUtils.randomAlphabetic(8);
        String mid = RandomStringUtils.randomAlphabetic(6);
        Integer age = ThreadLocalRandom.current().nextInt(18, 120);
        List<Account> accounts = new ArrayList<>();

        Client client = new Client(id, last, first, mid, age, accounts);
        log.debug("client: " + client);

        return client;
    }

    private Account createAccount(Client client, Long accountId) {
        String number = RandomStringUtils.randomNumeric(20);
        Date openingDate = new Date();
        BigDecimal balance = BigDecimal.valueOf(0);
        List<Card> cards = new ArrayList<>();

        Account account = new Account(accountId, number, openingDate, balance, cards);
        log.debug("account: " + account);

        List<Account> accounts = client.getAccounts();
        accounts.add(account);
        log.debug("Клиент с добавленным аккаунтом: " + client);

        return account;
    }

    private Card createCard(Client client, Account account, Long cardId) {
        String cardNumber = RandomStringUtils.randomNumeric(16);
        Date releaseDate = new Date();
        Card card = new Card(cardId, cardNumber, releaseDate);
        log.debug("card: " + card);

        List<Card> cards = account.getCards();
        cards.add(card);
        log.debug("Аккаунт с добавленной картой: " + account);
        log.debug("Клиент с добавленной картой: " + client);

        return card;
    }

    @Test
    @DisplayName("Неуспешное добавление карты по счёту: клиент не найден")
    public void saveFailClient() {
        Long clientId = 1L;
        Long accountId = 10L;

        Mockito.when(clientRepository.findById(clientId))
                .thenReturn(Optional.empty());

        try {
            cardService.save(clientId, accountId);
        } catch (ClientNotFoundException e) {
            log.debug(e.getMessage());
            assertThat(e.getMessage()).isEqualTo("Не найден клиент по id=" + clientId);
        }
    }

    @Test
    @DisplayName("Неуспешное добавление карты по счёту: счёт не найден")
    public void saveFailAccount() {
        Long clientId = 1L;
        Long accountId = 10L;
        Client client = createClient(clientId);
        Account account = createAccount(client, accountId);

        Long nonExistentAccountId = accountId + 1L;
        log.debug("nonExistentAccountId: " + nonExistentAccountId);

        Mockito.when(clientRepository.findById(clientId))
                .thenReturn(Optional.of(client));

        try {
            cardService.save(clientId, nonExistentAccountId);
        } catch (AccountNotFoundException e) {
            log.debug(e.getMessage());
            assertThat(e.getMessage()).isEqualTo("У клиента с id=" + clientId + " отсутствует счёт с id=" + nonExistentAccountId);
        }
    }

    @Test
    @DisplayName("Неуспешное удаление карты: клиент не найден")
    public void deleteFailClient() {
        Long clientId = 1L;
        Long accountId = 10L;
        Long cardId = 100L;
        Client client = createClient(clientId);
        Account account = createAccount(client, accountId);
        Card card = createCard(client, account, cardId);

        Long nonExistentClientId = clientId + 1L;
        log.debug("nonExistentClientId: " + nonExistentClientId);

        Mockito.when(clientRepository.findById(nonExistentClientId))
                .thenReturn(Optional.empty());

        try {
            cardService.delete(nonExistentClientId, cardId);
        } catch (ClientNotFoundException e) {
            log.debug(e.getMessage());
            assertThat(e.getMessage()).isEqualTo("Не найден клиент по id=" + nonExistentClientId);
        }
    }

    @Test
    @DisplayName("Неуспешное удаление карты: карта не найдена")
    public void deleteFailCard() {
        Long clientId = 1L;
        Long accountId = 10L;
        Long cardId = 100L;
        Client client = createClient(clientId);
        Account account = createAccount(client, accountId);
        Card card = createCard(client, account, cardId);

        Long nonExistentCardId = cardId + 1L;
        log.debug("nonExistentCardId: " + nonExistentCardId);

        Mockito.when(clientRepository.findById(clientId))
                .thenReturn(Optional.of(client));

        try {
            cardService.delete(clientId, nonExistentCardId);
        } catch (CardNotFoundException e) {
            log.debug(e.getMessage());
            assertThat(e.getMessage()).isEqualTo("У клиента с id=" + clientId + " отсутствует карта с id=" + nonExistentCardId);
        }
    }
}
