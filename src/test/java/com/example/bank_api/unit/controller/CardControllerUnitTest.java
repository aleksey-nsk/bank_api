package com.example.bank_api.unit.controller;

import com.example.bank_api.controller.CardController;
import com.example.bank_api.dto.CardDto;
import com.example.bank_api.entity.Account;
import com.example.bank_api.entity.Card;
import com.example.bank_api.entity.Client;
import com.example.bank_api.exception.AccountNotFoundException;
import com.example.bank_api.exception.CardNotFoundException;
import com.example.bank_api.exception.ClientNotFoundException;
import com.example.bank_api.service.CardService;
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

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = CardController.class)
@Log4j2
@ActiveProfiles("test")
public class CardControllerUnitTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CardService cardService;

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
    @DisplayName("Успешное добавление карты по счёту")
    public void saveSuccess() throws Exception {
        Client client = createClient(1L);
        Account account = createAccount(client, 1L);
        CardDto saved = CardDto.valueOf(createCard(client, account, 1L));

        String savedJson = objectMapper.writeValueAsString(saved);
        log.debug("savedJson: " + savedJson);

        Mockito.when(cardService.save(client.getId(), account.getId()))
                .thenReturn(saved);

        mockMvc.perform(post(BASE_URL + client.getId() + "/account/" + account.getId() + "/card").content("").contentType(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().json(savedJson, true));
    }

    @Test
    @DisplayName("Неуспешное добавление карты по счёту: клиент не найден")
    public void saveFailClient() throws Exception {
        Long clientId = 1L;
        Long accountId = 1L;
        Client client = createClient(clientId);
        Account account = createAccount(client, accountId);

        Long nonExistentClientId = clientId + 1L;
        log.debug("nonExistentClientId: " + nonExistentClientId);

        Mockito.doThrow(new ClientNotFoundException(nonExistentClientId))
                .when(cardService).save(nonExistentClientId, accountId);

        String url = BASE_URL + nonExistentClientId + "/account/" + accountId + "/card";
        log.debug("url: " + url);

        mockMvc.perform(post(url).content("").contentType(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Неуспешное добавление карты по счёту: счёт не найден")
    public void saveFailAccount() throws Exception {
        Long clientId = 1L;
        Long accountId = 1L;
        Client client = createClient(clientId);
        Account account = createAccount(client, accountId);

        Long nonExistentAccountId = accountId + 1L;
        log.debug("nonExistentAccountId: " + nonExistentAccountId);

        Mockito.doThrow(new AccountNotFoundException("У клиента с id=" + clientId + " отсутствует счёт с id=" + nonExistentAccountId))
                .when(cardService).save(clientId, nonExistentAccountId);

        String url = BASE_URL + clientId + "/account/" + nonExistentAccountId + "/card";
        log.debug("url: " + url);

        mockMvc.perform(post(url).content("").contentType(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Успешное удаление карты")
    public void deleteSuccess() throws Exception {
        Client client = createClient(1L);
        Account account = createAccount(client, 1L);
        Card card = createCard(client, account, 1L);

        String url = BASE_URL + client.getId() + "/card/" + card.getId();
        log.debug("url: " + url);

        mockMvc.perform(delete(url).content("").contentType(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Неуспешное удаление карты: клиент не найден")
    public void deleteFailClient() throws Exception {
        Client client = createClient(1L);
        Account account = createAccount(client, 1L);
        Card card = createCard(client, account, 1L);

        Long nonExistentClientId = client.getId() + 1L;
        log.debug("nonExistentClientId: " + nonExistentClientId);

        Mockito.doThrow(new ClientNotFoundException(nonExistentClientId))
                .when(cardService).delete(nonExistentClientId, card.getId());

        String url = BASE_URL + nonExistentClientId + "/card/" + card.getId();
        log.debug("url: " + url);

        mockMvc.perform(delete(url).content("").contentType(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Неуспешное удаление карты: карта не найдена")
    public void deleteFailCard() throws Exception {
        Client client = createClient(1L);
        Account account = createAccount(client, 1L);
        Card card = createCard(client, account, 1L);

        Long nonExistentCardId = card.getId() + 1L;
        log.debug("nonExistentCardId: " + nonExistentCardId);

        Mockito.doThrow(new CardNotFoundException("У клиента с id=" + client.getId() + " отсутствует карта с id=" + nonExistentCardId))
                .when(cardService).delete(client.getId(), nonExistentCardId);

        String url = BASE_URL + client.getId() + "/card/" + nonExistentCardId;
        log.debug("url: " + url);

        mockMvc.perform(delete(url).content("").contentType(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }
}
