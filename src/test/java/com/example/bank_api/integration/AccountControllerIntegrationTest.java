package com.example.bank_api.integration;

import com.example.bank_api.dto.AccountDto;
import com.example.bank_api.dto.CardDto;
import com.example.bank_api.entity.Account;
import com.example.bank_api.entity.Card;
import com.example.bank_api.entity.Client;
import com.example.bank_api.repository.AccountRepository;
import com.example.bank_api.repository.CardRepository;
import com.example.bank_api.repository.ClientRepository;
import com.example.bank_api.service.AccountService;
import com.example.bank_api.service.CardService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang.RandomStringUtils;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Log4j2
@ActiveProfiles("test")
public class AccountControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private CardRepository cardRepository;

    @Autowired
    private AccountService accountService;

    @Autowired
    private CardService cardService;

    private static final String BASE_URL = "/api/v1/client/";

    @AfterEach
    void tearDown() {
        cardRepository.deleteAll();
        accountRepository.deleteAll();
        clientRepository.deleteAll();
    }

    private Client saveClientInDB() {
        String last = RandomStringUtils.randomAlphabetic(10);
        String first = RandomStringUtils.randomAlphabetic(8);
        String mid = RandomStringUtils.randomAlphabetic(6);
        Integer age = ThreadLocalRandom.current().nextInt(18, 120);
        List<Account> accounts = Collections.emptyList();

        Client client = new Client(last, first, mid, age, accounts);
        log.debug("client: " + client);

        Client savedClient = clientRepository.save(client);
        log.debug("savedClient: " + savedClient);

        return savedClient;
    }

    private Account saveAccountInDB(Client client) {
        AccountDto savedAccountDto = accountService.save(client.getId());
        return savedAccountDto.mapToAccount();
    }

    private Card saveCardInDB(Client client, Account account) {
        CardDto savedCardDto = cardService.save(client.getId(), account.getId());
        return savedCardDto.mapToCard();
    }

    @Test
    @DisplayName("Успешный поиск всех счетов клиента")
    public void findAllSuccess() throws Exception {
        Client client = saveClientInDB();
        AccountDto account1 = AccountDto.valueOf(saveAccountInDB(client));
        AccountDto account2 = AccountDto.valueOf(saveAccountInDB(client));

        assertThat(clientRepository.findAll()).size().isEqualTo(1);
        assertThat(accountRepository.findAll()).size().isEqualTo(2);
        assertThat(cardRepository.findAll()).size().isEqualTo(0);

        List<AccountDto> list = new ArrayList<>();
        list.add(account1);
        list.add(account2);
        log.debug("list: " + list);

        String savedAsJson = objectMapper.writeValueAsString(list);
        log.debug("savedAsJson: " + savedAsJson);

        mockMvc.perform(get(BASE_URL + client.getId() + "/account"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(savedAsJson, true));
    }

    @Test
    @DisplayName("Неуспешный поиск всех счетов клиента: клиент не найден")
    public void findAllFail() throws Exception {
        Client client = saveClientInDB();
        AccountDto account1 = AccountDto.valueOf(saveAccountInDB(client));
        AccountDto account2 = AccountDto.valueOf(saveAccountInDB(client));

        assertThat(clientRepository.findAll()).size().isEqualTo(1);
        assertThat(accountRepository.findAll()).size().isEqualTo(2);
        assertThat(cardRepository.findAll()).size().isEqualTo(0);

        Long nonExistentClientId = client.getId() + 1L;
        log.debug("nonExistentClientId: " + nonExistentClientId);

        mockMvc.perform(get(BASE_URL + nonExistentClientId + "/account"))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Успешный поиск счёта по id")
    public void findByIdSuccess() throws Exception {
        Client client = saveClientInDB();
        AccountDto account1 = AccountDto.valueOf(saveAccountInDB(client));
        AccountDto account2 = AccountDto.valueOf(saveAccountInDB(client));

        assertThat(clientRepository.findAll()).size().isEqualTo(1);
        assertThat(accountRepository.findAll()).size().isEqualTo(2);
        assertThat(cardRepository.findAll()).size().isEqualTo(0);

        String savedAsJson = objectMapper.writeValueAsString(account2);
        log.debug("savedAsJson: " + savedAsJson);

        mockMvc.perform(get(BASE_URL + client.getId() + "/account/" + account2.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(savedAsJson, true));
    }

    @Test
    @DisplayName("Счёт по id не найден: клиент не найден")
    public void findByIdFailClient() throws Exception {
        Client client = saveClientInDB();
        AccountDto account = AccountDto.valueOf(saveAccountInDB(client));

        assertThat(clientRepository.findAll()).size().isEqualTo(1);
        assertThat(accountRepository.findAll()).size().isEqualTo(1);
        assertThat(cardRepository.findAll()).size().isEqualTo(0);

        Long nonExistentClientId = client.getId() + 1L;
        log.debug("nonExistentClientId: " + nonExistentClientId);

        mockMvc.perform(get(BASE_URL + nonExistentClientId + "/account/" + account.getId()))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Счёт по id не найден: счёт не найден")
    public void findByIdFailAccount() throws Exception {
        Client client1 = saveClientInDB();
        AccountDto account1 = AccountDto.valueOf(saveAccountInDB(client1));

        Client client2 = saveClientInDB();
        AccountDto account2 = AccountDto.valueOf(saveAccountInDB(client2));

        assertThat(clientRepository.findAll()).size().isEqualTo(2);
        assertThat(accountRepository.findAll()).size().isEqualTo(2);
        assertThat(cardRepository.findAll()).size().isEqualTo(0);

        mockMvc.perform(get(BASE_URL + client2.getId() + "/account/" + account1.getId()))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Успешное добавление счёта клиенту (без карт)")
    public void saveSuccess() throws Exception {
        Client client = saveClientInDB();

        assertThat(clientRepository.findAll()).size().isEqualTo(1);
        assertThat(accountRepository.findAll()).size().isEqualTo(0);
        assertThat(cardRepository.findAll()).size().isEqualTo(0);

        mockMvc.perform(post(BASE_URL + client.getId() + "/account").content("").contentType(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.number").exists())
                .andExpect(jsonPath("$.openingDate").exists())
                .andExpect(jsonPath("$.balance").value(0))
                .andExpect(jsonPath("$.cards").isArray());

        AssertionsForClassTypes.assertThat(clientRepository.findAll().size()).isEqualTo(1);
        AssertionsForClassTypes.assertThat(accountRepository.findAll().size()).isEqualTo(1);
        AssertionsForClassTypes.assertThat(cardRepository.findAll().size()).isEqualTo(0);
    }

    @Test
    @DisplayName("Неуспешное добавление счёта клиенту: клиент не найден")
    public void saveFail() throws Exception {
        Client client = saveClientInDB();

        assertThat(clientRepository.findAll()).size().isEqualTo(1);
        assertThat(accountRepository.findAll()).size().isEqualTo(0);
        assertThat(cardRepository.findAll()).size().isEqualTo(0);

        Long nonExistentClientId = client.getId() + 1L;
        log.debug("nonExistentClientId: " + nonExistentClientId);

        mockMvc.perform(post(BASE_URL + nonExistentClientId + "/account").content("").contentType(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());

        assertThat(clientRepository.findAll()).size().isEqualTo(1);
        assertThat(accountRepository.findAll()).size().isEqualTo(0);
        assertThat(cardRepository.findAll()).size().isEqualTo(0);
    }

    @Test
    @DisplayName("Успешное обновление счёта: добавлены деньги к текущему балансу")
    public void updateSuccess() throws Exception {
        Client client = saveClientInDB();
        Account account = saveAccountInDB(client);
        Card card = saveCardInDB(client, account);

        assertThat(clientRepository.findAll()).size().isEqualTo(1);
        assertThat(accountRepository.findAll()).size().isEqualTo(1);
        assertThat(cardRepository.findAll()).size().isEqualTo(1);

        Long val = ThreadLocalRandom.current().nextLong(10, 100);
        BigDecimal add = BigDecimal.valueOf(val);
        log.debug("add: " + add);

        String url = BASE_URL + client.getId() + "/account/card?cardNumber=" + card.getCardNumber() + "&add=" + add;
        log.debug("url: " + url);

        mockMvc.perform(put(url).content("").contentType(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());

        AssertionsForClassTypes.assertThat(clientRepository.findAll().size()).isEqualTo(1);
        AssertionsForClassTypes.assertThat(accountRepository.findAll().size()).isEqualTo(1);
        AssertionsForClassTypes.assertThat(cardRepository.findAll().size()).isEqualTo(1);

        Account updated = accountRepository.findById(account.getId()).get();

        assertThat(updated.getId()).isEqualTo(account.getId());
        assertThat(updated.getNumber()).isEqualTo(account.getNumber());
        assertThat(updated.getOpeningDate().getTime()).isEqualTo(account.getOpeningDate().getTime());
        assertThat(updated.getBalance().compareTo(account.getBalance().add(add))).isEqualTo(0);
    }

    @Test
    @DisplayName("Неуспешное обновление счёта: клиент не найден")
    public void updateFailClient() throws Exception {
        Client client = saveClientInDB();
        Account account = saveAccountInDB(client);
        Card card = saveCardInDB(client, account);

        assertThat(clientRepository.findAll()).size().isEqualTo(1);
        assertThat(accountRepository.findAll()).size().isEqualTo(1);
        assertThat(cardRepository.findAll()).size().isEqualTo(1);

        Long nonExistentClientId = client.getId() + 1L;
        log.debug("nonExistentClientId: " + nonExistentClientId);

        Long val = ThreadLocalRandom.current().nextLong(10, 100);
        BigDecimal add = BigDecimal.valueOf(val);
        log.debug("add: " + add);

        String url = BASE_URL + nonExistentClientId + "/account/card?cardNumber=" + card.getCardNumber() + "&add=" + add;
        log.debug("url: " + url);

        mockMvc.perform(put(url).content("").contentType(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());

        AssertionsForClassTypes.assertThat(clientRepository.findAll().size()).isEqualTo(1);
        AssertionsForClassTypes.assertThat(accountRepository.findAll().size()).isEqualTo(1);
        AssertionsForClassTypes.assertThat(cardRepository.findAll().size()).isEqualTo(1);

        Account notUpdated = accountRepository.findById(account.getId()).get();

        assertThat(notUpdated.getId()).isEqualTo(account.getId());
        assertThat(notUpdated.getNumber()).isEqualTo(account.getNumber());
        assertThat(notUpdated.getOpeningDate().getTime()).isEqualTo(account.getOpeningDate().getTime());
        assertThat(notUpdated.getBalance().compareTo(account.getBalance())).isEqualTo(0);
    }

    @Test
    @DisplayName("Неуспешное обновление счёта: карта не найдена")
    public void updateFailCard() throws Exception {
        Client client1 = saveClientInDB();
        Account account1 = saveAccountInDB(client1);
        Card card1 = saveCardInDB(client1, account1);

        Client client2 = saveClientInDB();
        Account account2 = saveAccountInDB(client2);
        Card card2 = saveCardInDB(client2, account2);

        assertThat(clientRepository.findAll()).size().isEqualTo(2);
        assertThat(accountRepository.findAll()).size().isEqualTo(2);
        assertThat(cardRepository.findAll()).size().isEqualTo(2);

        Long val = ThreadLocalRandom.current().nextLong(10, 100);
        BigDecimal add = BigDecimal.valueOf(val);
        log.debug("add: " + add);

        String url = BASE_URL + client1.getId() + "/account/card?cardNumber=" + card2.getCardNumber() + "&add=" + add;
        log.debug("url: " + url);

        mockMvc.perform(put(url).content("").contentType(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());

        AssertionsForClassTypes.assertThat(clientRepository.findAll().size()).isEqualTo(2);
        AssertionsForClassTypes.assertThat(accountRepository.findAll().size()).isEqualTo(2);
        AssertionsForClassTypes.assertThat(cardRepository.findAll().size()).isEqualTo(2);

        Account notUpdated1 = accountRepository.findById(account1.getId()).get();
        assertThat(notUpdated1.getId()).isEqualTo(account1.getId());
        assertThat(notUpdated1.getNumber()).isEqualTo(account1.getNumber());
        assertThat(notUpdated1.getOpeningDate().getTime()).isEqualTo(account1.getOpeningDate().getTime());
        assertThat(notUpdated1.getBalance().compareTo(account1.getBalance())).isEqualTo(0);

        Account notUpdated2 = accountRepository.findById(account2.getId()).get();
        assertThat(notUpdated2.getId()).isEqualTo(account2.getId());
        assertThat(notUpdated2.getNumber()).isEqualTo(account2.getNumber());
        assertThat(notUpdated2.getOpeningDate().getTime()).isEqualTo(account2.getOpeningDate().getTime());
        assertThat(notUpdated2.getBalance().compareTo(account2.getBalance())).isEqualTo(0);
    }
}
