package com.example.bank_api.integration;

import com.example.bank_api.dto.AccountDto;
import com.example.bank_api.dto.CardDto;
import com.example.bank_api.dto.ClientDto;
import com.example.bank_api.entity.Account;
import com.example.bank_api.entity.Client;
import com.example.bank_api.repository.AccountRepository;
import com.example.bank_api.repository.CardRepository;
import com.example.bank_api.repository.ClientRepository;
import com.example.bank_api.service.AccountService;
import com.example.bank_api.service.CardService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang.RandomStringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Log4j2
@ActiveProfiles("test")
public class CardControllerMockMvcIntegrationTest {

    private static final String BASE_URL = "/api/v1/client";

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

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        cardRepository.deleteAll();
        accountRepository.deleteAll();
        clientRepository.deleteAll();
    }

    private ClientDto saveClientInDB() {
        String last = RandomStringUtils.randomAlphabetic(10);
        String first = RandomStringUtils.randomAlphabetic(8);
        String mid = RandomStringUtils.randomAlphabetic(6);
        Integer age = ThreadLocalRandom.current().nextInt(18, 120);
        List<Account> accounts = Collections.emptyList();

        Client client = new Client(last, first, mid, age, accounts);
        log.debug("client: " + client);

        Client savedClient = clientRepository.save(client);
        log.debug("savedClient: " + savedClient);

        ClientDto savedClientDto = ClientDto.valueOf(savedClient);
        log.debug("savedClientDto: " + savedClientDto);

        return savedClientDto;
    }

    private AccountDto saveAccountInDB(Long clientId) {
        return accountService.save(clientId);
    }

    private CardDto saveCardInDB(Long clientId, Long accountId) {
        return cardService.save(clientId, accountId);
    }

    @Test
    @DisplayName("[Integration] Успешный поиск всех карт клиента")
    public void findAllSuccess() throws Exception {
        Long savedClientId = saveClientInDB().getId();
        Long savedAccountId = saveAccountInDB(savedClientId).getId();

        CardDto savedCardDto1 = saveCardInDB(savedClientId, savedAccountId);
        CardDto savedCardDto2 = saveCardInDB(savedClientId, savedAccountId);

        List<CardDto> list = new ArrayList<>();
        list.add(savedCardDto1);
        list.add(savedCardDto2);

        String savedAsJson = objectMapper.writeValueAsString(list);
        log.debug("savedAsJson: " + savedAsJson);

        String url = BASE_URL + "/" + savedClientId + "/card";
        log.debug("url: " + url);

        mockMvc.perform(get(url))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(savedAsJson, true));

        assertThat(clientRepository.findAll().size()).isEqualTo(1);
        assertThat(accountRepository.findAll().size()).isEqualTo(1);
        assertThat(cardRepository.findAll().size()).isEqualTo(2);
    }

    @Test
    @DisplayName("[Integration] Успешное добавление карты по счёту")
    public void saveSuccess() throws Exception {
        Long savedClientId = saveClientInDB().getId();
        Long savedAccountId = saveAccountInDB(savedClientId).getId();

        String url = BASE_URL + "/" + savedClientId + "/account/" + savedAccountId + "/card";
        log.debug("url: " + url);

        mockMvc.perform(post(url))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.number").isString())
                .andExpect(jsonPath("$.releaseDate").isNotEmpty());

        assertThat(clientRepository.findAll().size()).isEqualTo(1);
        assertThat(accountRepository.findAll().size()).isEqualTo(1);
        assertThat(cardRepository.findAll().size()).isEqualTo(1);
    }

    @Test
    @DisplayName("[Integration] Успешное удаление карты")
    public void deleteSuccess() throws Exception {
        Long savedClientId = saveClientInDB().getId();
        Long savedAccountId = saveAccountInDB(savedClientId).getId();
        Long savedCardId = saveCardInDB(savedClientId, savedAccountId).getId();

        String url = BASE_URL + "/" + savedClientId + "/card/" + savedCardId;
        log.debug("url: " + url);

        mockMvc.perform(delete(url))
                .andDo(print())
                .andExpect(status().isNoContent());

        assertThat(clientRepository.findAll().size()).isEqualTo(1);
        assertThat(accountRepository.findAll().size()).isEqualTo(1);
        assertThat(cardRepository.findAll().size()).isEqualTo(0);
    }
}
