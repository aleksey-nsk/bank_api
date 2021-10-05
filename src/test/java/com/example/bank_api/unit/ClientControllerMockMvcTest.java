package com.example.bank_api.unit;

import com.example.bank_api.controller.ClientController;
import com.example.bank_api.entity.Client;
import com.example.bank_api.service.ClientService;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@WebMvcTest(controllers = ClientController.class)
@Log4j2
public class ClientControllerMockMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ClientService clientService;

    private static final String BASE_URL = "/api/v1/client";

    @Test
    public void findAllSuccess() throws Exception {
        Long id = 1L;
        String last = "Ivanov";
        String first = "Ivan";
        String mid = "Ivanovich";
        Integer age = 22;

        Client client = new Client(id, last, first, mid, age, Collections.emptyList());
        log.debug("client: " + client);

        List<Client> clientList = new ArrayList<>();
        clientList.add(client);
        log.debug("clientList: " + clientList);

        Mockito.doReturn(clientList)
                .when(clientService).findAll();

        String expectedBody = "[\n" +
                "    {\n" +
                "        \"id\": " + id + ",\n" +
                "        \"lastname\": \"" + last + "\",\n" +
                "        \"firstname\": \"" + first + "\",\n" +
                "        \"middlename\": \"" + mid + "\",\n" +
                "        \"age\": " + age + ",\n" +
                "        \"accounts\": []\n" +
                "    }\n" +
                "]";
        log.debug("expectedBody: " + expectedBody);

        mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(expectedBody, true));
    }
}
