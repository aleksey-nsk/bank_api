package com.example.bank_api.integration;

import com.example.bank_api.entity.Account;
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
import org.springframework.test.web.servlet.ResultHandler;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Collections;
import java.util.List;

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
//        Client client = new Client();
//        client.setLastname("test1");
//        client.setFirstname("test2");
//        client.setMiddlename("test3");
//        client.setAge(25);
//        List<Account> list = Collections.emptyList();
//        client.setAccounts(list);
//        log.debug("client: " + client);

        String last = "Ivanov";
        String first = "Ivan";
        String mid = "Ivanovich";
        Integer age = 33;

        Client client = new Client(last, first, mid, age);
        log.debug("client: " + client);

        Client saved = clientRepository.save(client);
        log.debug("saved: " + saved);

//        String body1 = "[{\"id\":1,\"lastname\":\"Иванов\",\"firstname\":\"Иван\",\"middlename\":\"Иванович\",\"age\":34," +
//                "\"accounts\":[{\"id\":1,\"number\":\"11111222223333344444\",\"openingDate\":\"2017-01-13T17:00:00.000+00:00\",\"balance\":100," +
//                "\"cards\":[{\"id\":1,\"number\":\"0000001111122222\",\"releaseDate\":\"2019-09-14T17:00:00.000+00:00\"}," +
//                "{\"id\":2,\"number\":\"0000003333344444\",\"releaseDate\":\"2019-10-15T17:00:00.000+00:00\"}]}," +
//                "{\"id\":2,\"number\":\"55555666667777788888\",\"openingDate\":\"2018-02-14T17:00:00.000+00:00\",\"balance\":500," +
//                "\"cards\":[{\"id\":3,\"number\":\"0000005555566666\",\"releaseDate\":\"2020-11-16T17:00:00.000+00:00\"}," +
//                "{\"id\":4,\"number\":\"0000007777788888\",\"releaseDate\":\"2020-12-17T17:00:00.000+00:00\"}]}]}," +
//                "{\"id\":2,\"lastname\":\"test1\",\"firstname\":\"test2\",\"middlename\":\"test3\",\"age\":25,\"accounts\":[]}]";

        String body = "[\n" +
                "    {\n" +
                "        \"id\": " + saved.getId() + ",\n" +
                "        \"lastname\": \""+last+"\",\n" +
                "        \"firstname\": \"""\",\n" +
                "        \"middlename\": \"Иванович\",\n" +
                "        \"age\": 34,\n" +
                "        \"accounts\": []\n" +
                "    }\n" +
                "]";
        log.debug("body: " + body);

        mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(body, true));
    }
}
