package com.example.bank_api.unit.controller;

import com.example.bank_api.controller.ClientController;
import com.example.bank_api.dto.ClientDto;
import com.example.bank_api.entity.Account;
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

import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ClientController.class)
@Log4j2
@ActiveProfiles("test")
public class ClientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ClientService clientService;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String BASE_URL = "/api/v1/client";

    private ClientDto createClient(Long id) {
        String last = RandomStringUtils.randomAlphabetic(10);
        String first = RandomStringUtils.randomAlphabetic(8);
        String mid = RandomStringUtils.randomAlphabetic(6);
        Integer age = ThreadLocalRandom.current().nextInt(18, 120);
        List<Account> accounts = Collections.emptyList();

        ClientDto clientDto = new ClientDto(id, last, first, mid, age, accounts);
        log.debug("clientDto: " + clientDto);

        return clientDto;
    }

    @Test
    @DisplayName("Успешный поиск всех клиентов")
    public void findAllSuccess() throws Exception {
        ClientDto created1 = createClient(1L);
        ClientDto created2 = createClient(2L);

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
        ClientDto created = createClient(id);

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
        ClientDto saved = createClient(1L);

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

        when(clientService.save(client))
                .thenReturn(saved);

        mockMvc.perform(post(BASE_URL).content(clientJson).contentType(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().json(savedJson, true));
    }

    @Test
    @DisplayName("Дубликат клиента по ФИО в БД не добавлен")
    public void saveFail() throws Exception {
        ClientDto saved = createClient(1L);
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
        ClientDto updateDto = createClient(null); // данные для обновления без айдишника

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
        ClientDto updateDto = createClient(null);

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
