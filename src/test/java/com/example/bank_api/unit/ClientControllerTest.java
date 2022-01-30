//package com.example.bank_api.unit;
//
//import com.example.bank_api.controller.ClientController;
//import com.example.bank_api.dto.ClientDto;
//import com.example.bank_api.entity.Account;
//import com.example.bank_api.entity.Client;
//import com.example.bank_api.service.ClientService;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import lombok.extern.log4j.Log4j2;
//import org.apache.commons.lang.RandomStringUtils;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mockito;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.test.web.servlet.MockMvc;
//
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.List;
//import java.util.concurrent.ThreadLocalRandom;
//
//import static org.springframework.http.MediaType.APPLICATION_JSON;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@WebMvcTest(controllers = ClientController.class)
//@Log4j2
//public class ClientControllerTest {
//
//    private static final String BASE_URL = "/api/v1/client";
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @MockBean
//    private ClientService clientService;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    private ClientDto createClient(Long id) {
//        String last = RandomStringUtils.randomAlphabetic(10);
//        String first = RandomStringUtils.randomAlphabetic(8);
//        String mid = RandomStringUtils.randomAlphabetic(6);
//        Integer age = ThreadLocalRandom.current().nextInt(18, 120);
//
//        Client client = new Client(id, last, first, mid, age, Collections.emptyList());
//        log.debug("client: " + client);
//
//        ClientDto clientDto = ClientDto.valueOf(client);
//        log.debug("clientDto: " + clientDto);
//
//        return clientDto;
//    }
//
//    @Test
//    @DisplayName("[Controller] Успешный поиск всех клиентов")
//    public void findAllSuccess() throws Exception {
//        ClientDto created1 = createClient(1L);
//        ClientDto created2 = createClient(2L);
//
//        List<ClientDto> list = new ArrayList<>();
//        list.add(created1);
//        list.add(created2);
//
//        String expectedJson = objectMapper.writeValueAsString(list);
//        log.debug("expectedJson: " + expectedJson);
//
//        Mockito.doReturn(list)
//                .when(clientService).findAll();
//
//        mockMvc.perform(get(BASE_URL))
//                .andDo(print())
//                .andExpect(status().isOk())
//                .andExpect(content().json(expectedJson, true));
//    }
//
//    @Test
//    @DisplayName("[Controller] Успешный поиск клиента по id")
//    public void findByIdSuccess() throws Exception {
//        Long id = 1L;
//        ClientDto created = createClient(id);
//
//        String expectedJson = objectMapper.writeValueAsString(created);
//        log.debug("expectedJson: " + expectedJson);
//
//        Mockito.doReturn(created)
//                .when(clientService).findById(id);
//
//        mockMvc.perform(get(BASE_URL + "/" + id))
//                .andDo(print())
//                .andExpect(status().isOk())
//                .andExpect(content().json(expectedJson, true));
//    }
//
//    @Test
//    @DisplayName("[Controller] Клиент по id не найден")
//    public void findByIdFail() throws Exception {
//        Long id = 1L;
//
//        Mockito.doReturn(null)
//                .when(clientService).findById(id);
//
//        mockMvc.perform(get(BASE_URL + "/" + id))
//                .andDo(print())
//                .andExpect(status().isNotFound());
//    }
//
//    @Test
//    @DisplayName("[Controller] Успешное добавление клиента без счетов")
//    public void saveSuccess() throws Exception {
//        Long id = 1L;
//        String last = "Last";
//        String first = "First";
//        String mid = "Mid";
//        Integer age = 44;
//        List<Account> accounts = Collections.emptyList();
//
//        ClientDto client = new ClientDto(last, first, mid, age, accounts);
//        ClientDto saved = new ClientDto(id, last, first, mid, age, accounts);
//        log.debug("client: " + client);
//        log.debug("saved: " + saved);
//
//        String clientJson = objectMapper.writeValueAsString(client);
//        String savedJson = objectMapper.writeValueAsString(saved);
//        log.debug("clientJson: " + clientJson);
//        log.debug("savedJson: " + savedJson);
//
//        Mockito.when(clientService.save(client))
//                .thenReturn(saved);
//
//        mockMvc.perform(
//                        post(BASE_URL)
//                                .content(clientJson)
//                                .contentType(APPLICATION_JSON)
//                )
//                .andDo(print())
//                .andExpect(status().isCreated())
//                .andExpect(content().json(savedJson, true));
//    }
//}
