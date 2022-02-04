package com.example.bank_api.unit.controller;

import com.example.bank_api.controller.ClientController;
import com.example.bank_api.dto.ClientDto;
import com.example.bank_api.entity.Account;
import com.example.bank_api.entity.Client;
import com.example.bank_api.exception.ClientDuplicateException;
import com.example.bank_api.exception.ClientNotFoundException;
import com.example.bank_api.service.ClientService;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// Неудобный способ запросов Spring обернул в Spring MockMvc.
// MockMvc можно использовать и для ИНТЕГРАЦИОННЫХ и для ЮНИТ-ТЕСТОВ.
//
// Ещё в Спринге есть специальные АННОТАЦИИ-СРЕЗЫ.
// Для MockMvc тоже есть специальный срез:
// @WebMvcTest(controllers = ClientController.class)
//   - указываем контроллер который хотим тестировать (controllers = ClientController.class);
//   - аннотация @WebMvcTest содержит в себе аннотацию @AutoConfigureMockMvc и прочие.
//
// UNIT-ТЕСТ => тестю только слой контроллеров. Для этого помечаю класс
// аннотацией @WebMvcTest (эта аннотация создаст только бин контроллера; также будет создан бин MockMvc).

@WebMvcTest(controllers = ClientController.class)
@Log4j2
@ActiveProfiles("test")
public class ClientControllerUnitTest {

    @Autowired
    private MockMvc mockMvc;

    // Т.к. в контроллере есть сервис, то его придётся МОКАТЬ,
    // потому что он уже не проинициализируется.
    // В итоге сервис будет не настоящим (в каждом тестовом методе
    // будем имитировать его поведение)
    @MockBean
    private ClientService clientService;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String BASE_URL = "/api/v1/client";

    private Client createClient(Long id) {
        String last = RandomStringUtils.randomAlphabetic(10);
        String first = RandomStringUtils.randomAlphabetic(8);
        String mid = RandomStringUtils.randomAlphabetic(6);
        Integer age = ThreadLocalRandom.current().nextInt(18, 120);
        List<Account> accounts = Collections.emptyList();

        Client client = new Client(id, last, first, mid, age, accounts);
        log.debug("client: " + client);

        return client;
    }

    @Test
    @DisplayName("Успешный поиск всех клиентов")
    public void findAllSuccess() throws Exception {
        ClientDto created1 = ClientDto.valueOf(createClient(1L));
        ClientDto created2 = ClientDto.valueOf(createClient(2L));

        List<ClientDto> list = new ArrayList<>();
        list.add(created1);
        list.add(created2);
        log.debug("list: " + list);

        String expectedJson = objectMapper.writeValueAsString(list);
        log.debug("expectedJson: " + expectedJson);

        Mockito.doReturn(list)
                .when(clientService).findAll();

        mockMvc.perform(get(BASE_URL))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson, true));
    }

    @Test
    @DisplayName("Успешный поиск клиента по id")
    public void findByIdSuccess() throws Exception {
        Long id = 1L;
        ClientDto created = ClientDto.valueOf(createClient(id));

        String expectedJson = objectMapper.writeValueAsString(created);
        log.debug("expectedJson: " + expectedJson);

        Mockito.doReturn(created)
                .when(clientService).findById(id);

        mockMvc.perform(get(BASE_URL + "/" + id))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson, true));
    }

    @Test
    @DisplayName("Клиент по id не найден")
    public void findByIdFail() throws Exception {
        Long id = 1L;

        Mockito.doThrow(new ClientNotFoundException(id))
                .when(clientService).findById(id);

        mockMvc.perform(get(BASE_URL + "/" + id))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Успешное добавление клиента без счетов")
    public void saveSuccess() throws Exception {
        // С айдишником
        ClientDto saved = ClientDto.valueOf(createClient(1L));

        // Без айдишника
        ClientDto client = new ClientDto(
                saved.getLastname(),
                saved.getFirstname(),
                saved.getMiddlename(),
                saved.getAge(),
                saved.getAccounts()
        );
        log.debug("client: " + client);

        String savedJson = objectMapper.writeValueAsString(saved);
        String clientJson = objectMapper.writeValueAsString(client);
        log.debug("savedJson: " + savedJson);
        log.debug("clientJson: " + clientJson);

        Mockito.when(clientService.save(client))
                .thenReturn(saved);

        mockMvc.perform(post(BASE_URL).content(clientJson).contentType(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().json(savedJson, true));
    }

    @Test
    @DisplayName("Дубликат клиента по ФИО в БД не добавлен")
    public void saveFail() throws Exception {
        ClientDto saved = ClientDto.valueOf(createClient(1L));
        String last = saved.getLastname();
        String first = saved.getFirstname();
        String mid = saved.getMiddlename();

        // Создаём дубликат по ФИО
        ClientDto duplicateDto = new ClientDto(
                last,
                first,
                mid,
                ThreadLocalRandom.current().nextInt(18, 120),
                Collections.emptyList()
        );
        log.debug("duplicateDto: " + duplicateDto);

        String duplicateDtoAsJson = objectMapper.writeValueAsString(duplicateDto);
        log.debug("duplicateDtoAsJson: " + duplicateDtoAsJson);

        String message = String.format("В БД уже есть клиент с полным именем '%s %s %s'", last, first, mid);
        Mockito.doThrow(new ClientDuplicateException(message))
                .when(clientService).save(duplicateDto);

        mockMvc.perform(post(BASE_URL).content(duplicateDtoAsJson).contentType(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    @DisplayName("Успешное обновление клиента")
    public void updateSuccess() throws Exception {
        Long id = 1L;
        ClientDto updateDto = ClientDto.valueOf(createClient(null)); // данные для обновления без айдишника

        String updateAsJson = objectMapper.writeValueAsString(updateDto);
        log.debug("updateAsJson: " + updateAsJson);

        mockMvc.perform(put(BASE_URL + "/" + id).content(updateAsJson).contentType(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Клиент для обновления не найден")
    public void updateFail() throws Exception {
        Long id = 1L;
        ClientDto updateDto = ClientDto.valueOf(createClient(null));

        String updateAsJson = objectMapper.writeValueAsString(updateDto);
        log.debug("updateAsJson: " + updateAsJson);

        Mockito.doThrow(new ClientNotFoundException(id))
                .when(clientService).update(id, updateDto);

        mockMvc.perform(put(BASE_URL + "/" + id).content(updateAsJson).contentType(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Успешное удаление клиента")
    public void deleteSuccess() throws Exception {
        Long id = 1L;

        mockMvc.perform(delete(BASE_URL + "/" + id))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Клиент для удаления не найден")
    public void deleteFail() throws Exception {
        Long id = 1L;

        Mockito.doThrow(new ClientNotFoundException(id))
                .when(clientService).delete(id);

        mockMvc.perform(delete(BASE_URL + "/" + id))
                .andDo(print())
                .andExpect(status().isNotFound());
    }
}
