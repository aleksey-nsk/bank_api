package com.example.bank_api.integration;

import com.example.bank_api.dto.AccountDto;
import com.example.bank_api.entity.Account;
import com.example.bank_api.entity.Card;
import com.example.bank_api.entity.Client;
import com.example.bank_api.repository.AccountRepository;
import com.example.bank_api.repository.CardRepository;
import com.example.bank_api.repository.ClientRepository;
import com.example.bank_api.service.AccountService;
import com.example.bank_api.service.CardService;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang.RandomStringUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Log4j2
@ActiveProfiles("test")
public class CardControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

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

    @Test
    @DisplayName("Успешное добавление карты по счёту")
    public void saveSuccess() throws Exception {
        Client client = saveClientInDB();
        Account account = saveAccountInDB(client);

        assertThat(clientRepository.findAll()).size().isEqualTo(1);
        assertThat(accountRepository.findAll()).size().isEqualTo(1);
        assertThat(cardRepository.findAll()).size().isEqualTo(0);

        String url = BASE_URL + client.getId() + "/account/" + account.getId() + "/card";
        log.debug("url: " + url);

        mockMvc.perform(post(url).content("").contentType(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.cardNumber").exists())
                .andExpect(jsonPath("$.releaseDate").exists());

        assertThat(clientRepository.findAll().size()).isEqualTo(1);
        assertThat(accountRepository.findAll().size()).isEqualTo(1);
        assertThat(cardRepository.findAll().size()).isEqualTo(1);
    }

    @Test
    @DisplayName("Неуспешное добавление карты по счёту: клиент не найден")
    public void saveFailClient() throws Exception {
        Client client = saveClientInDB();
        Account account = saveAccountInDB(client);

        assertThat(clientRepository.findAll()).size().isEqualTo(1);
        assertThat(accountRepository.findAll()).size().isEqualTo(1);
        assertThat(cardRepository.findAll()).size().isEqualTo(0);

        Long nonExistentClientId = client.getId() + 1L;
        log.debug("nonExistentClientId: " + nonExistentClientId);

        String url = BASE_URL + nonExistentClientId + "/account/" + account.getId() + "/card";
        log.debug("url: " + url);

        mockMvc.perform(post(url).content("").contentType(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());

        assertThat(clientRepository.findAll().size()).isEqualTo(1);
        assertThat(accountRepository.findAll().size()).isEqualTo(1);
        assertThat(cardRepository.findAll().size()).isEqualTo(0);
    }

    @Test
    @DisplayName("Неуспешное добавление карты по счёту: счёт не найден")
    public void saveFailAccount() throws Exception {
        Client client1 = saveClientInDB();
        Account account1 = saveAccountInDB(client1);

        Client client2 = saveClientInDB();
        Account account2 = saveAccountInDB(client2);

        assertThat(clientRepository.findAll()).size().isEqualTo(2);
        assertThat(accountRepository.findAll()).size().isEqualTo(2);
        assertThat(cardRepository.findAll()).size().isEqualTo(0);

        String url = BASE_URL + client1.getId() + "/account/" + account2.getId() + "/card";
        log.debug("url: " + url);

        mockMvc.perform(post(url).content("").contentType(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());

        assertThat(clientRepository.findAll().size()).isEqualTo(2);
        assertThat(accountRepository.findAll().size()).isEqualTo(2);
        assertThat(cardRepository.findAll().size()).isEqualTo(0);
    }

    @Test
    @DisplayName("Успешное удаление карты")
    public void deleteSuccess() throws Exception {
        Client client = saveClientInDB();
        Account account = saveAccountInDB(client);
        Card card = cardService.save(client.getId(), account.getId()).mapToCard();

        assertThat(clientRepository.findAll()).size().isEqualTo(1);
        assertThat(accountRepository.findAll()).size().isEqualTo(1);
        assertThat(cardRepository.findAll()).size().isEqualTo(1);

        mockMvc.perform(delete(BASE_URL + client.getId() + "/card/" + card.getId()))
                .andDo(print())
                .andExpect(status().isNoContent());

        assertThat(clientRepository.findAll().size()).isEqualTo(1);
        assertThat(accountRepository.findAll().size()).isEqualTo(1);
        assertThat(cardRepository.findAll().size()).isEqualTo(0);
    }

    @Test
    @DisplayName("Неуспешное удаление карты: клиент не найден")
    public void deleteFailClient() throws Exception {
        Client client = saveClientInDB();
        Account account = saveAccountInDB(client);
        Card card = cardService.save(client.getId(), account.getId()).mapToCard();

        assertThat(clientRepository.findAll()).size().isEqualTo(1);
        assertThat(accountRepository.findAll()).size().isEqualTo(1);
        assertThat(cardRepository.findAll()).size().isEqualTo(1);

        Long nonExistentClientId = client.getId() + 1L;
        log.debug("nonExistentClientId: " + nonExistentClientId);

        mockMvc.perform(delete(BASE_URL + nonExistentClientId + "/card/" + card.getId()))
                .andDo(print())
                .andExpect(status().isNotFound());

        assertThat(clientRepository.findAll().size()).isEqualTo(1);
        assertThat(accountRepository.findAll().size()).isEqualTo(1);
        assertThat(cardRepository.findAll().size()).isEqualTo(1);
    }

    @Test
    @DisplayName("Неуспешное удаление карты: карта не найдена")
    public void deleteFailCard() throws Exception {
        Client client1 = saveClientInDB();
        Account account1 = saveAccountInDB(client1);
        Card card1 = cardService.save(client1.getId(), account1.getId()).mapToCard();

        Client client2 = saveClientInDB();
        Account account2 = saveAccountInDB(client2);
        Card card2 = cardService.save(client2.getId(), account2.getId()).mapToCard();

        assertThat(clientRepository.findAll()).size().isEqualTo(2);
        assertThat(accountRepository.findAll()).size().isEqualTo(2);
        assertThat(cardRepository.findAll()).size().isEqualTo(2);

        mockMvc.perform(delete(BASE_URL + client1.getId() + "/card/" + card2.getId()))
                .andDo(print())
                .andExpect(status().isNotFound());

        assertThat(clientRepository.findAll().size()).isEqualTo(2);
        assertThat(accountRepository.findAll().size()).isEqualTo(2);
        assertThat(cardRepository.findAll().size()).isEqualTo(2);
    }
}
