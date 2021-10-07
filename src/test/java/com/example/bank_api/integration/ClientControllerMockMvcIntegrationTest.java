package com.example.bank_api.integration;

import com.example.bank_api.dto.ClientDto;
import com.example.bank_api.entity.Account;
import com.example.bank_api.entity.Client;
import com.example.bank_api.repository.AccountRepository;
import com.example.bank_api.repository.CardRepository;
import com.example.bank_api.repository.ClientRepository;
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

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Log4j2
@ActiveProfiles("test")
public class ClientControllerMockMvcIntegrationTest {

    private static final String BASE_URL = "/api/v1/client";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CardRepository cardRepository;

    @Autowired
    private AccountRepository accountRepository;

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

    private ClientDto createClient() {
        String last = RandomStringUtils.randomAlphabetic(10);
        String first = RandomStringUtils.randomAlphabetic(8);
        String mid = RandomStringUtils.randomAlphabetic(6);
        Integer age = ThreadLocalRandom.current().nextInt(18, 120);

        Client client = new Client(last, first, mid, age);
        log.debug("client: " + client);

        ClientDto clientDto = ClientDto.valueOf(client);
        log.debug("clientDto: " + clientDto);

        return clientDto;
    }

    @Test
    @DisplayName("[Integration] Успешный поиск всех клиентов")
    public void findAllSuccess() throws Exception {
        ClientDto saved1 = saveClientInDB();
        ClientDto saved2 = saveClientInDB();

        List<ClientDto> list = new ArrayList<>();
        list.add(saved1);
        list.add(saved2);

        String savedAsJson = objectMapper.writeValueAsString(list);
        log.debug("savedAsJson: " + savedAsJson);

        mockMvc.perform(get(BASE_URL))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(savedAsJson, true));
    }

    @Test
    @DisplayName("[Integration] Успешный поиск клиента по id")
    public void findByIdSuccess() throws Exception {
        ClientDto saved = saveClientInDB();

        String savedAsJson = objectMapper.writeValueAsString(saved);
        log.debug("savedAsJson: " + savedAsJson);

        mockMvc.perform(get(BASE_URL + "/" + saved.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(savedAsJson, true));
    }

    @Test
    @DisplayName("[Integration] Клиент по id не найден")
    public void findByIdFail() throws Exception {
        mockMvc.perform(get(BASE_URL + "/999"))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("[Integration] Успешное добавление клиента без счетов")
    public void saveSuccess() throws Exception {
        ClientDto clientDto = createClient();

        String clientDtoAsJson = objectMapper.writeValueAsString(clientDto);
        log.debug("clientDtoAsJson: " + clientDtoAsJson);

        mockMvc.perform(
                        post(BASE_URL)
                                .content(clientDtoAsJson)
                                .contentType(APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.lastname").value(clientDto.getLastname()))
                .andExpect(jsonPath("$.firstname").value(clientDto.getFirstname()))
                .andExpect(jsonPath("$.middlename").value(clientDto.getMiddlename()))
                .andExpect(jsonPath("$.age").value(clientDto.getAge()));
    }

    @Test
    @DisplayName("[Integration] Успешное обновление клиента")
    public void updateSuccess() throws Exception {
        ClientDto saved = saveClientInDB();
        ClientDto update = createClient();

        String updateAsJson = objectMapper.writeValueAsString(update);
        log.debug("updateAsJson: " + updateAsJson);

        mockMvc.perform(
                        put(BASE_URL + "/" + saved.getId())
                                .content(updateAsJson)
                                .contentType(APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("[Integration] Успешное удаление клиента")
    public void deleteSuccess() throws Exception {
        ClientDto saved = saveClientInDB();

        mockMvc.perform(delete(BASE_URL + "/" + saved.getId()))
                .andDo(print())
                .andExpect(status().isNoContent());
    }
}
