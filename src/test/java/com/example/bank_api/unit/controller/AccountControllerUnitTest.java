package com.example.bank_api.unit.controller;

import com.example.bank_api.controller.AccountController;
import com.example.bank_api.dto.AccountDto;
import com.example.bank_api.entity.Account;
import com.example.bank_api.entity.Card;
import com.example.bank_api.entity.Client;
import com.example.bank_api.exception.AccountNotFoundException;
import com.example.bank_api.exception.CardNotFoundException;
import com.example.bank_api.exception.ClientNotFoundException;
import com.example.bank_api.service.AccountService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang.RandomStringUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AccountController.class)
@Log4j2
@ActiveProfiles("test")
public class AccountControllerUnitTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AccountService accountService;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String BASE_URL = "/api/v1/client/";

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
    public void findAllSuccess() throws Exception {
        Long clientId = 1L;
        Client client = createClient(clientId);
        createAccount(client, 1L);
        createAccount(client, 2L);

        List<AccountDto> list = client.getAccounts()
                .stream()
                .map(it -> AccountDto.valueOf(it))
                .collect(Collectors.toList());
        log.debug("list: " + list);

        String expectedJson = objectMapper.writeValueAsString(list);
        log.debug("expectedJson: " + expectedJson);

        Mockito.doReturn(list)
                .when(accountService).findAll(clientId);

        mockMvc.perform(get(BASE_URL + clientId + "/account"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson, true));
    }

    @Test
    @DisplayName("Неуспешный поиск всех счетов клиента: клиент не найден")
    public void findAllFail() throws Exception {
        Long nonExistentClientId = 1L;
        log.debug("nonExistentClientId: " + nonExistentClientId);

        Mockito.doThrow(new ClientNotFoundException(nonExistentClientId))
                .when(accountService).findAll(nonExistentClientId);

        mockMvc.perform(get(BASE_URL + nonExistentClientId + "/account"))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Успешный поиск счёта по id")
    public void findByIdSuccess() throws Exception {
        Long clientId = 1L;
        Client client = createClient(clientId);

        Long accountId1 = 1L;
        Long accountId2 = 2L;
        AccountDto accountDto1 = AccountDto.valueOf(createAccount(client, accountId1));
        AccountDto accountDto2 = AccountDto.valueOf(createAccount(client, accountId2));

        String expectedJson = objectMapper.writeValueAsString(accountDto2);
        log.debug("expectedJson: " + expectedJson);

        Mockito.doReturn(accountDto2)
                .when(accountService).findById(clientId, accountId2);

        mockMvc.perform(get(BASE_URL + clientId + "/account/" + accountId2))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson, true));
    }

    @Test
    @DisplayName("Счёт по id не найден: клиент не найден")
    public void findByIdFailClient() throws Exception {
        Long clientId = 1L;
        Client client = createClient(clientId);

        Long accountId = 1L;
        createAccount(client, accountId);

        Long nonExistentClientId = clientId + 1L;
        log.debug("nonExistentClientId: " + nonExistentClientId);

        Mockito.doThrow(new ClientNotFoundException(nonExistentClientId))
                .when(accountService).findById(nonExistentClientId, accountId);

        mockMvc.perform(get(BASE_URL + nonExistentClientId + "/account/" + accountId))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Счёт по id не найден: счёт не найден")
    public void findByIdFailAccount() throws Exception {
        Long clientId = 1L;
        Client client = createClient(clientId);

        Long accountId = 1L;
        createAccount(client, accountId);

        Long nonExistentAccountId = accountId + 1L;
        log.debug("nonExistentAccountId: " + nonExistentAccountId);

        Mockito.doThrow(new AccountNotFoundException("У клиента с id=" + clientId + " отсутствует счёт с id=" + nonExistentAccountId))
                .when(accountService).findById(clientId, nonExistentAccountId);

        mockMvc.perform(get(BASE_URL + clientId + "/account/" + nonExistentAccountId))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Успешное добавление счёта клиенту (без карт)")
    public void saveSuccess() throws Exception {
        Long clientId = 1L;
        Client client = createClient(clientId);
        AccountDto saved = AccountDto.valueOf(createAccount(client, 1L));

        String savedJson = objectMapper.writeValueAsString(saved);
        log.debug("savedJson: " + savedJson);

        Mockito.when(accountService.save(clientId))
                .thenReturn(saved);

        mockMvc.perform(post(BASE_URL + clientId + "/account").content("").contentType(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().json(savedJson, true));
    }

    @Test
    @DisplayName("Неуспешное добавление счёта клиенту: клиент не найден")
    public void saveFail() throws Exception {
        Long clientId = 1L;
        Client client = createClient(clientId);

        Long nonExistentClientId = clientId + 1L;
        log.debug("nonExistentClientId: " + nonExistentClientId);

        Mockito.doThrow(new ClientNotFoundException(nonExistentClientId))
                .when(accountService).save(nonExistentClientId);

        mockMvc.perform(post(BASE_URL + nonExistentClientId + "/account").content("").contentType(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Успешное обновление счёта: добавлены деньги к текущему балансу")
    public void updateSuccess() throws Exception {
        Client client = createClient(1L);
        Account account = createAccount(client, 1L);
        Card card = createCard(client, account, 1L);

        Long val = ThreadLocalRandom.current().nextLong(10, 100);
        BigDecimal add = BigDecimal.valueOf(val);
        log.debug("add: " + add);

        String url = BASE_URL + client.getId() + "/account/card?cardNumber=" + card.getCardNumber() + "&add=" + add;
        log.debug("url: " + url);

        mockMvc.perform(put(url).content("").contentType(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Неуспешное обновление счёта: клиент не найден")
    public void updateFailClient() throws Exception {
        Client client = createClient(1L);
        Account account = createAccount(client, 1L);
        Card card = createCard(client, account, 1L);

        Long nonExistentClientId = client.getId() + 1L;
        log.debug("nonExistentClientId: " + nonExistentClientId);

        Long val = ThreadLocalRandom.current().nextLong(10, 100);
        BigDecimal add = BigDecimal.valueOf(val);
        log.debug("add: " + add);

        String url = BASE_URL + nonExistentClientId + "/account/card?cardNumber=" + card.getCardNumber() + "&add=" + add;
        log.debug("url: " + url);

        Mockito.doThrow(new ClientNotFoundException(nonExistentClientId))
                .when(accountService).updateAccountAddBalanceByCardNumber(nonExistentClientId, card.getCardNumber(), add);

        mockMvc.perform(put(url).content("").contentType(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Неуспешное обновление счёта: карта не найдена")
    public void updateFailCard() throws Exception {
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

        String url = BASE_URL + clientId1 + "/account/card?cardNumber=" + cardNumber2 + "&add=" + add;
        log.debug("url: " + url);

        Mockito.doThrow(new CardNotFoundException("У клиента с id=" + clientId1 + " отсутствует карта с номером=" + cardNumber2))
                .when(accountService).updateAccountAddBalanceByCardNumber(clientId1, cardNumber2, add);

        mockMvc.perform(put(url).content("").contentType(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }
}
