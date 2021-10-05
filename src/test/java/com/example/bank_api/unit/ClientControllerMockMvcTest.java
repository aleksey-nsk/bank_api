package com.example.bank_api.unit;

import com.example.bank_api.controller.ClientController;
import com.example.bank_api.entity.Account;
import com.example.bank_api.entity.Client;
import com.example.bank_api.service.ClientService;
//import com.sun.tools.javac.util.List;
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
public class ClientControllerMockMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ClientService clientService;

    private static final String BASE_URL = "/api/v1/client";

    @Test
    public void findAllSuccess() throws Exception {
        Client client = new Client();
        client.setId(10L); // !!!!!!!!!!!!
        client.setLastname("test1");
        client.setFirstname("test2");
        client.setMiddlename("test3");
        client.setAge(25);
        List<Account> list = Collections.emptyList();
        client.setAccounts(list);
        System.out.println("client: " + client);

//        Client saved = clientRepository.save(client);
//        System.out.println("saved: " + saved);

        List<Client> clientList = new ArrayList<>();
        clientList.add(client);

        Mockito.doReturn(clientList)
                .when(clientService).findAll();



        String body = "[{\"id\":10,\"lastname\":\"test1\",\"firstname\":\"test2\"," +
                "\"middlename\":\"test3\",\"age\":25,\"accounts\":[]}]";

        mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(body, true));
    }
}
