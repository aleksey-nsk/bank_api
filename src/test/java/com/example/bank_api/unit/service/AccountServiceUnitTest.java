package com.example.bank_api.unit.service;

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
import com.example.bank_api.service.impl.AccountServiceImpl;
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
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = AccountServiceImpl.class)
@Log4j2
@ActiveProfiles("test")
public class AccountServiceUnitTest {

    @Autowired
    private AccountService accountService;

    @MockBean
    private ClientRepository clientRepository;

    @MockBean
    private AccountRepository accountRepository;

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
    @DisplayName("Успешный поиск всех счетов клиента")
    public void findAllSuccess() {
        Long clientId = 1L;
        Client client = createClient(clientId);
        createAccount(client, 1L);
        createAccount(client, 2L);

        List<AccountDto> accountDtoList = client.getAccounts()
                .stream()
                .map(it -> AccountDto.valueOf(it))
                .collect(Collectors.toList());
        log.debug("accountDtoList: " + accountDtoList);

        Mockito.doReturn(Optional.of(client))
                .when(clientRepository).findById(clientId);

        List<AccountDto> actual = accountService.findAll(clientId);
        log.debug("actual: " + actual);

        assertThat(actual).size().isEqualTo(2);
        assertThat(actual).isEqualTo(accountDtoList);
    }

    @Test
    @DisplayName("Неуспешный поиск всех счетов клиента: клиент не найден")
    public void findAllFail() {
        Long clientId = 1L;

        Mockito.doReturn(Optional.empty())
                .when(clientRepository).findById(clientId);

        try {
            accountService.findAll(clientId);
        } catch (ClientNotFoundException e) {
            log.debug(e.getMessage());
            assertThat(e.getMessage()).isEqualTo("Не найден клиент по id=" + clientId);
        }
    }

    @Test
    @DisplayName("Успешный поиск счёта по id")
    public void findByIdSuccess() {
        Long clientId = 1L;
        Long accountId = 1L;
        Long cardId = 1L;
        Client client = createClient(clientId);
        Account account = createAccount(client, accountId);
        Card card = createCard(client, account, cardId);

        AccountDto accountDto = AccountDto.valueOf(account);

        Mockito.doReturn(Optional.of(client))
                .when(clientRepository).findById(clientId);

        AccountDto actual = accountService.findById(clientId, accountId);
        log.debug("actual: " + actual);

        assertThat(actual).isNotNull();
        assertThat(actual).isEqualTo(accountDto);
    }

    @Test
    @DisplayName("Счёт по id не найден: клиент не найден")
    public void findByIdFailClient() {
        Long clientId = 1L;
        Long accountId = 1L;

        Mockito.doReturn(Optional.empty())
                .when(clientRepository).findById(clientId);

        try {
            accountService.findById(clientId, accountId);
        } catch (ClientNotFoundException e) {
            log.debug(e.getMessage());
            assertThat(e.getMessage()).isEqualTo("Не найден клиент по id=" + clientId);
        }
    }

    @Test
    @DisplayName("Счёт по id не найден: счёт не найден")
    public void findByIdFailAccount() {
        Long clientId = 1L;
        Long accountId = 1L;
        Client client = createClient(clientId);
        Account account = createAccount(client, accountId);

        Long nonExistentAccountId = accountId + 1L;
        log.debug("nonExistentAccountId: " + nonExistentAccountId);

        Mockito.doReturn(Optional.of(client))
                .when(clientRepository).findById(clientId);

        try {
            accountService.findById(clientId, nonExistentAccountId);
        } catch (AccountNotFoundException e) {
            log.debug(e.getMessage());
            assertThat(e.getMessage()).isEqualTo("У клиента с id=" + clientId + " отсутствует счёт с id=" + nonExistentAccountId);
        }
    }

    @Test
    @DisplayName("Неуспешное добавление счёта клиенту: клиент не найден")
    public void saveFail() {
        Long clientId = 1L;

        Mockito.when(clientRepository.findById(clientId))
                .thenReturn(Optional.empty());

        try {
            accountService.save(clientId);
        } catch (ClientNotFoundException e) {
            log.debug(e.getMessage());
            assertThat(e.getMessage()).isEqualTo("Не найден клиент по id=" + clientId);
        }
    }

    @Test
    @DisplayName("Неуспешное обновление счёта: клиент не найден")
    public void updateFailClient() {
        Long clientId = 1L;
        Long accountId = 1L;
        Long cardId = 1L;
        Client client = createClient(clientId);
        Account account = createAccount(client, accountId);
        Card card = createCard(client, account, cardId);

        Long val = ThreadLocalRandom.current().nextLong(10, 100);
        BigDecimal add = BigDecimal.valueOf(val);
        log.debug("add: " + add);

        Long nonExistentClientId = clientId + 1L;
        log.debug("nonExistentClientId: " + nonExistentClientId);

        Mockito.doReturn(Optional.empty())
                .when(clientRepository).findById(nonExistentClientId);

        try {
            accountService.updateAccountAddBalanceByCardNumber(nonExistentClientId, card.getCardNumber(), add);
        } catch (ClientNotFoundException e) {
            log.debug(e.getMessage());
            assertThat(e.getMessage()).isEqualTo("Не найден клиент по id=" + nonExistentClientId);
        }
    }

    @Test
    @DisplayName("Неуспешное обновление счёта: карта не найдена")
    public void updateFailCard() {
        Client client1 = createClient(1L);
        Account account1 = createAccount(client1, 1L);
        Card card1 = createCard(client1, account1, 1L);

        Client client2 = createClient(1L);
        Account account2 = createAccount(client2, 1L);
        Card card2 = createCard(client2, account2, 1L);

        Long clientId1 = client1.getId();
        String cardNumber2 = card2.getCardNumber();

        Long val = ThreadLocalRandom.current().nextLong(10, 100);
        BigDecimal add = BigDecimal.valueOf(val);
        log.debug("add: " + add);

        Mockito.doReturn(Optional.of(client1))
                .when(clientRepository).findById(clientId1);

        try {
            accountService.updateAccountAddBalanceByCardNumber(clientId1, cardNumber2, add);
        } catch (CardNotFoundException e) {
            log.debug(e.getMessage());
            assertThat(e.getMessage()).isEqualTo("У клиента с id=" + clientId1 + " отсутствует карта с номером=" + cardNumber2);
        }
    }
}
