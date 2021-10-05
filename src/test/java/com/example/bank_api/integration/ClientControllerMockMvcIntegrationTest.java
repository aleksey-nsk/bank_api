package com.example.bank_api.integration;

import com.example.bank_api.entity.Client;
import com.example.bank_api.repository.ClientRepository;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@AutoConfigureMockMvc
@Log4j2
public class ClientControllerMockMvcIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    private static final String BASE_URL = "/api/v1/client";

    @Autowired
    private ClientRepository clientRepository;

    @BeforeEach
    void setUp() {
        log.debug("BEFORE EACH");
        clientRepository.deleteAll();
    }

    @AfterEach
    void tearDown() {
        log.debug("AFTER EACH");
    }

    @Test
    public void findAllSuccess() throws Exception {
        String last = "Ivanov";
        String first = "Ivan";
        String mid = "Ivanovich";
        Integer age = 33;

        Client client = new Client(last, first, mid, age);
        log.debug("client: " + client);

        Client saved = clientRepository.save(client);
        log.debug("saved: " + saved);

        String expectedBody = "[\n" +
                "    {\n" +
                "        \"id\": " + saved.getId() + ",\n" +
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
