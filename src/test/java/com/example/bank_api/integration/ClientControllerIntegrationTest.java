package com.example.bank_api.integration;

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
import org.junit.jupiter.api.AfterEach;
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

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// Неудобный способ запросов Spring обернул в Spring MockMvc.
// MockMvc можно использовать и для ИНТЕГРАЦИОННЫХ и для ЮНИТ-ТЕСТОВ.
//
// Теперь уже не надо указывать ХОСТ и ПОРТ,
// потому что мы всё делаем в ЗАМОКАННОМ КОНТЕКСТЕ.
//
// Для интеграционных тестов используем аннотации @SpringBootTest и @AutoConfigureMockMvc.
// Аннотация @AutoConfigureMockMvc нужна для того, чтобы появилась возможность ВНЕДРИТЬ
// в тестовый класс БИН MockMvc.
//
// С TestRestTemplate мы по-настоящему запускали сервер,
// тогда как с MockMvc - нет.
//
// При помощи MockMvc можем КИДАТЬ ЗАПРОСЫ В НАШ КОНТРОЛЛЕР.
// Класс MockMvc предназначен для тестирования контроллеров.
// Он позволяет тестировать контроллеры БЕЗ ЗАПУСКА HTTP-СЕРВЕРА
// (т.е. при выполнении тестов сетевое соединение не создаётся).

@SpringBootTest
@AutoConfigureMockMvc
@Log4j2
@ActiveProfiles("test")
public class ClientControllerIntegrationTest {

    // Благодаря аннотации @AutoConfigureMockMvc могу
    // добавить специальный бин MockMvc в наш контекст
    @Autowired
    private MockMvc mockMvc;

    // ObjectMapper - этот класс преобразует объект в JSON-строку
    // (он нужен т.к. мы тестим REST API, а MockMvc самостоятельно это преобразование не делает)
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

    private static final String BASE_URL = "/api/v1/client";

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

    private Client createClient() {
        String last = RandomStringUtils.randomAlphabetic(10);
        String first = RandomStringUtils.randomAlphabetic(8);
        String mid = RandomStringUtils.randomAlphabetic(6);
        Integer age = ThreadLocalRandom.current().nextInt(18, 120);
        List<Account> accounts = Collections.emptyList();

        Client client = new Client(last, first, mid, age, accounts);
        log.debug("client: " + client);

        return client;
    }

    @Test
    @DisplayName("Успешный поиск всех клиентов")
    public void findAllSuccess() throws Exception {
        ClientDto saved1 = ClientDto.valueOf(saveClientInDB());
        ClientDto saved2 = ClientDto.valueOf(saveClientInDB());

        List<ClientDto> list = new ArrayList<>();
        list.add(saved1);
        list.add(saved2);
        log.debug("list: " + list);

        String savedAsJson = objectMapper.writeValueAsString(list);
        log.debug("savedAsJson: " + savedAsJson);

        mockMvc.perform(get(BASE_URL))
                .andDo(print()) // распечатать в консоль MockHttpServletRequest и MockHttpServletResponse
                .andExpect(status().isOk()) // ожидаем статус
                .andExpect(content().json(savedAsJson, true)); // ожидаем JSON
    }

    @Test
    @DisplayName("Успешный поиск клиента по id")
    public void findByIdSuccess() throws Exception {
        ClientDto saved = ClientDto.valueOf(saveClientInDB());

        String savedAsJson = objectMapper.writeValueAsString(saved);
        log.debug("savedAsJson: " + savedAsJson);

        mockMvc.perform(get(BASE_URL + "/" + saved.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(savedAsJson, true));
    }

    @Test
    @DisplayName("Клиент по id не найден")
    public void findByIdFail() throws Exception {
        mockMvc.perform(get(BASE_URL + "/1"))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Успешное добавление клиента без счетов")
    public void saveSuccess() throws Exception {
        ClientDto clientDto = ClientDto.valueOf(createClient());

        String clientDtoAsJson = objectMapper.writeValueAsString(clientDto);
        log.debug("clientDtoAsJson: " + clientDtoAsJson);

        mockMvc.perform(post(BASE_URL).content(clientDtoAsJson).contentType(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.lastname").value(clientDto.getLastname()))
                .andExpect(jsonPath("$.firstname").value(clientDto.getFirstname()))
                .andExpect(jsonPath("$.middlename").value(clientDto.getMiddlename()))
                .andExpect(jsonPath("$.age").value(clientDto.getAge()));

        assertThat(clientRepository.findAll().size()).isEqualTo(1);
        assertThat(accountRepository.findAll().size()).isEqualTo(0);
        assertThat(cardRepository.findAll().size()).isEqualTo(0);
    }

    @Test
    @DisplayName("Дубликат клиента по ФИО в БД не добавлен")
    public void saveFail() throws Exception {
        ClientDto saved = ClientDto.valueOf(saveClientInDB());

        // Создаём дубликат по ФИО
        Client duplicate = new Client(
                saved.getLastname(),
                saved.getFirstname(),
                saved.getMiddlename(),
                ThreadLocalRandom.current().nextInt(18, 120),
                Collections.emptyList()
        );
        ClientDto duplicateDto = ClientDto.valueOf(duplicate);
        log.debug("duplicateDto: " + duplicateDto);

        String duplicateDtoAsJson = objectMapper.writeValueAsString(duplicateDto);
        log.debug("duplicateDtoAsJson: " + duplicateDtoAsJson);

        mockMvc.perform(post(BASE_URL).content(duplicateDtoAsJson).contentType(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity());

        assertThat(clientRepository.findAll().size()).isEqualTo(1);
        assertThat(accountRepository.findAll().size()).isEqualTo(0);
        assertThat(cardRepository.findAll().size()).isEqualTo(0);
    }

    @Test
    @DisplayName("Успешное обновление клиента")
    public void updateSuccess() throws Exception {
        Long id = saveClientInDB().getId();
        ClientDto updateDto = ClientDto.valueOf(createClient());

        String updateAsJson = objectMapper.writeValueAsString(updateDto);
        log.debug("updateAsJson: " + updateAsJson);

        mockMvc.perform(put(BASE_URL + "/" + id).content(updateAsJson).contentType(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());

        assertThat(clientRepository.findAll().size()).isEqualTo(1);
        assertThat(accountRepository.findAll().size()).isEqualTo(0);
        assertThat(cardRepository.findAll().size()).isEqualTo(0);

        Client updated = clientRepository.findById(id).get();
        log.debug("updated: " + updated);

        assertThat(updated.getLastname()).isEqualTo(updateDto.getLastname());
        assertThat(updated.getFirstname()).isEqualTo(updateDto.getFirstname());
        assertThat(updated.getMiddlename()).isEqualTo(updateDto.getMiddlename());
        assertThat(updated.getAge()).isEqualTo(updateDto.getAge());
    }

    @Test
    @DisplayName("Клиент для обновления не найден")
    public void updateFail() throws Exception {
        Long id = 1L;
        ClientDto updateDto = ClientDto.valueOf(createClient());

        String updateAsJson = objectMapper.writeValueAsString(updateDto);
        log.debug("updateAsJson: " + updateAsJson);

        mockMvc.perform(put(BASE_URL + "/" + id).content(updateAsJson).contentType(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());

        assertThat(clientRepository.findAll().size()).isEqualTo(0);
        assertThat(accountRepository.findAll().size()).isEqualTo(0);
        assertThat(cardRepository.findAll().size()).isEqualTo(0);
    }

    @Test
    @DisplayName("Успешное удаление клиента без счетов")
    public void deleteSuccess() throws Exception {
        Long id = saveClientInDB().getId();

        mockMvc.perform(delete(BASE_URL + "/" + id))
                .andDo(print())
                .andExpect(status().isNoContent());

        assertThat(clientRepository.findAll().size()).isEqualTo(0);
        assertThat(accountRepository.findAll().size()).isEqualTo(0);
        assertThat(cardRepository.findAll().size()).isEqualTo(0);
    }

    @Test
    @DisplayName("Успешное удаление клиента со счетами и картами")
    public void deleteWithAccountsAndCardsSuccess() throws Exception {
        Long clientId = saveClientInDB().getId();
        Long accountId = accountService.save(clientId).getId();
        cardService.save(clientId, accountId);

        Client clientWithAccountsAndCards = clientRepository.findById(clientId).get();
        log.debug("clientWithAccountsAndCards: " + clientWithAccountsAndCards);

        assertThat(clientRepository.findAll()).size().isEqualTo(1);
        assertThat(accountRepository.findAll()).size().isEqualTo(1);
        assertThat(cardRepository.findAll()).size().isEqualTo(1);

        mockMvc.perform(delete(BASE_URL + "/" + clientId))
                .andDo(print())
                .andExpect(status().isNoContent());

        assertThat(clientRepository.findAll().size()).isEqualTo(0);
        assertThat(accountRepository.findAll().size()).isEqualTo(0);
        assertThat(cardRepository.findAll().size()).isEqualTo(0);
    }

    @Test
    @DisplayName("Клиент для удаления не найден")
    public void deleteFail() throws Exception {
        Long id = 1L;

        mockMvc.perform(delete(BASE_URL + "/" + id))
                .andDo(print())
                .andExpect(status().isNotFound());

        assertThat(clientRepository.findAll().size()).isEqualTo(0);
        assertThat(accountRepository.findAll().size()).isEqualTo(0);
        assertThat(cardRepository.findAll().size()).isEqualTo(0);
    }
}
