package com.example.bank_api.integration;

import com.example.bank_api.dto.ClientDto;
import com.example.bank_api.entity.Account;
import com.example.bank_api.entity.Client;
import com.example.bank_api.repository.ClientRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
public class ClientControllerIntegrationTest {

    private static final String BASE_URL = "/api/v1/client";

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private TestRestTemplate testRestTemplate;

    @LocalServerPort
    private String port;

    @Test
    public void findByIdSuccess() {
        Client client = new Client();
        client.setLastname("test1");
        client.setFirstname("test2");
        client.setMiddlename("test3");
        client.setAge(25);
        List<Account> list = Collections.emptyList();
        client.setAccounts(list);
        System.out.println("client: " + client);

        Client saved = clientRepository.save(client);
        System.out.println("saved: " + saved);

        String url = "http://localhost:" + port + BASE_URL + "/" + saved.getId();
        System.out.println("url: " + url);

        ResponseEntity<ClientDto> actual = testRestTemplate.getForEntity(url, ClientDto.class);
        System.out.println("actual: " + actual);

        HttpStatus statusCode = actual.getStatusCode();
        System.out.println("statusCode: " + statusCode);

        ClientDto body = actual.getBody();
        System.out.println("body: " + body);

        Assertions.assertThat(actual.getStatusCode())
                .isEqualTo(HttpStatus.OK);

        Assertions.assertThat(actual.getBody())
                .isNotNull();
    }

    @Test
    public void findByIdFail() {
//        Client client = new Client();
//        client.setLastname("test1");
//        client.setFirstname("test2");
//        client.setMiddlename("test3");
//        client.setAge(25);
//        List<Account> list = Collections.emptyList();
//        client.setAccounts(list);
//        System.out.println("client: " + client);
//
//        Client saved = clientRepository.save(client);
//        System.out.println("saved: " + saved);

        String url = "http://localhost:" + port + BASE_URL + "/100";
        System.out.println("url: " + url);

        ResponseEntity<ClientDto> actual = testRestTemplate.getForEntity(url, ClientDto.class);
        System.out.println("actual: " + actual);

        HttpStatus statusCode = actual.getStatusCode();
        System.out.println("statusCode: " + statusCode);

        ClientDto body = actual.getBody();
        System.out.println("body: " + body);

        Assertions.assertThat(actual.getStatusCode())
                .isEqualTo(HttpStatus.OK);

        Assertions.assertThat(actual.getBody())
                .isNotNull();
    }
}

/*
1) видос 42:20
2) Тестирование контроллеров https://sysout.ru/testirovanie-kontrollerov-s-pomoshhyu-mockmvc/
3) Профили https://sysout.ru/spring-profiles/
4) https://coderoad.ru/13364112/Spring-%D0%BF%D1%80%D0%BE%D1%84%D0%B8%D0%BB%D0%B8-%D0%B8-%D1%82%D0%B5%D1%81%D1%82%D0%B8%D1%80%D0%BE%D0%B2%D0%B0%D0%BD%D0%B8%D0%B5
5) https://coderoad.ru/39690094/Spring-boot-%D0%BF%D1%80%D0%BE%D1%84%D0%B8%D0%BB%D1%8C-%D0%BF%D0%BE-%D1%83%D0%BC%D0%BE%D0%BB%D1%87%D0%B0%D0%BD%D0%B8%D1%8E-%D0%B4%D0%BB%D1%8F-%D0%B8%D0%BD%D1%82%D0%B5%D0%B3%D1%80%D0%B0%D1%86%D0%B8%D0%BE%D0%BD%D0%BD%D1%8B%D1%85-%D1%82%D0%B5%D1%81%D1%82%D0%BE%D0%B2

 */
