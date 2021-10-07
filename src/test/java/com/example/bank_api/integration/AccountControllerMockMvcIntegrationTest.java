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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Log4j2
@ActiveProfiles("test")
public class AccountControllerMockMvcIntegrationTest {

    private static final String BASE_URL = "/api/v1/client";

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

    @Autowired
    private ObjectMapper objectMapper;

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
    @DisplayName("[Integration] Успешный поиск всех счетов клиента")
    public void findAllSuccess() throws Exception {
        Long savedClientId = saveClientInDB().getId();

        AccountDto savedAccountDto1 = saveAccountInDB(savedClientId);
        AccountDto savedAccountDto2 = saveAccountInDB(savedClientId);

        List<AccountDto> list = new ArrayList<>();
        list.add(savedAccountDto1);
        list.add(savedAccountDto2);

        String savedAsJson = objectMapper.writeValueAsString(list);
        log.debug("savedAsJson: " + savedAsJson);

        String url = BASE_URL + "/" + savedClientId + "/account";
        log.debug("url: " + url);

        mockMvc.perform(get(url))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(savedAsJson, true));
    }

    @Test
    @DisplayName("[Integration] Успешное добавление счёта без карт")
    public void saveSuccess() throws Exception {
        Long savedClientId = saveClientInDB().getId();

        String url = BASE_URL + "/" + savedClientId + "/account";
        log.debug("url: " + url);

        mockMvc.perform(post(url))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.number").isString())
                .andExpect(jsonPath("$.openingDate").isNotEmpty())
                .andExpect(jsonPath("$.balance").value(0))
                .andExpect(jsonPath("$.cards").isEmpty());
    }

    @Test
    @DisplayName("[Integration] Успешное внесение денег на счёт")
    public void updateSuccess() throws Exception {
        Long savedClientId = saveClientInDB().getId();
        Long savedAccountId = saveAccountInDB(savedClientId).getId();
        CardDto savedCardDto = saveCardInDB(savedClientId, savedAccountId);

        int add = ThreadLocalRandom.current().nextInt(1, 100);

        String url = BASE_URL + "/" + savedClientId + "/account/card?number=" + savedCardDto.getNumber() + "&add=" + add;
        log.debug("url: " + url);

        mockMvc.perform(put(url))
                .andDo(print())
                .andExpect(status().isOk());
    }
}
