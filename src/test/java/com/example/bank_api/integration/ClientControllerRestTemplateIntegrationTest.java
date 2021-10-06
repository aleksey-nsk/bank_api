package com.example.bank_api.integration;

import com.example.bank_api.dto.ClientDto;
import com.example.bank_api.entity.Account;
import com.example.bank_api.entity.Client;
import com.example.bank_api.repository.ClientRepository;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang.RandomStringUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@Log4j2
@ActiveProfiles("test")
public class ClientControllerRestTemplateIntegrationTest {

    private static final String BASE_URL = "/api/v1/client";

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private TestRestTemplate testRestTemplate;

    @LocalServerPort
    private String port;

    @AfterEach
    void tearDown() {
        clientRepository.deleteAll();
    }

    private ClientDto saveClient() {
        String last = RandomStringUtils.randomAlphabetic(10);
        String first = RandomStringUtils.randomAlphabetic(8);
        String mid = RandomStringUtils.randomAlphabetic(6);
        Integer age = 33;
        List<Account> accounts = Collections.emptyList();

        Client client = new Client(last, first, mid, age, accounts);
        log.debug("client: " + client);

        Client savedClient = clientRepository.save(client);
        log.debug("savedClient: " + savedClient);

        ClientDto savedClientDto = ClientDto.valueOf(savedClient);
        log.debug("savedClientDto: " + savedClientDto);

        return savedClientDto;
    }

    @Test
    @DisplayName("[TestRestTemplate] Успешный поиск всех клиентов")
    public void findAllSuccess() {
        ClientDto saved1 = saveClient();
        ClientDto saved2 = saveClient();

        String url = "http://localhost:" + port + BASE_URL;
        log.debug("url: " + url);

        ResponseEntity<List<ClientDto>> actual = testRestTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<ClientDto>>() {
                }
        );
        log.debug("actual: " + actual);

        HttpStatus statusCode = actual.getStatusCode();
        List<ClientDto> body = actual.getBody();
        ClientDto actual1 = body.get(0);
        ClientDto actual2 = body.get(1);

        assertThat(statusCode).isEqualTo(HttpStatus.OK);
        assertThat(body).isNotNull();

        assertThat(actual1.getId()).isEqualTo(saved1.getId());
        assertThat(actual1.getLastname()).isEqualTo(saved1.getLastname());
        assertThat(actual1.getFirstname()).isEqualTo(saved1.getFirstname());
        assertThat(actual1.getMiddlename()).isEqualTo(saved1.getMiddlename());
        assertThat(actual1.getAge()).isEqualTo(saved1.getAge());
        assertThat(actual1.getAccounts()).isEqualTo(Collections.emptyList());

        assertThat(actual2.getId()).isEqualTo(saved2.getId());
        assertThat(actual2.getLastname()).isEqualTo(saved2.getLastname());
        assertThat(actual2.getFirstname()).isEqualTo(saved2.getFirstname());
        assertThat(actual2.getMiddlename()).isEqualTo(saved2.getMiddlename());
        assertThat(actual2.getAge()).isEqualTo(saved2.getAge());
        assertThat(actual2.getAccounts()).isEqualTo(Collections.emptyList());
    }

    @Test
    @DisplayName("[TestRestTemplate] Успешный поиск клиента по id")
    public void findByIdSuccess() {
        ClientDto saved = saveClient();

        String url = "http://localhost:" + port + BASE_URL + "/" + saved.getId();
        log.debug("url: " + url);

        ResponseEntity<ClientDto> actual = testRestTemplate.getForEntity(url, ClientDto.class);
        log.debug("actual: " + actual);

        HttpStatus statusCode = actual.getStatusCode();
        ClientDto body = actual.getBody();

        assertThat(statusCode).isEqualTo(HttpStatus.OK);
        assertThat(body).isNotNull();
        assertThat(body.getId()).isEqualTo(saved.getId());
        assertThat(body.getLastname()).isEqualTo(saved.getLastname());
        assertThat(body.getFirstname()).isEqualTo(saved.getFirstname());
        assertThat(body.getMiddlename()).isEqualTo(saved.getMiddlename());
        assertThat(body.getAge()).isEqualTo(saved.getAge());
        assertThat(body.getAccounts()).isEqualTo(Collections.emptyList());
    }

    @Test
    @DisplayName("[TestRestTemplate] Клиент по id не найден")
    public void findByIdFail() {
        String url = "http://localhost:" + port + BASE_URL + "/1";
        log.debug("url: " + url);

        ResponseEntity<ClientDto> actual = testRestTemplate.getForEntity(url, ClientDto.class);
        log.debug("actual: " + actual);

        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("[TestRestTemplate] Успешное добавление клиента")
    public void saveSuccess() {
        String last = RandomStringUtils.randomAlphabetic(10);
        String first = RandomStringUtils.randomAlphabetic(8);
        String mid = RandomStringUtils.randomAlphabetic(6);
        Integer age = 44;
        List<Account> accounts = Collections.emptyList();
        Client client = new Client(last, first, mid, age, accounts);
        ClientDto clientDto = ClientDto.valueOf(client);
        log.debug("clientDto: " + clientDto);

        String url = "http://localhost:" + port + BASE_URL;
        log.debug("url: " + url);

        ResponseEntity<ClientDto> actual = testRestTemplate.postForEntity(url, clientDto, ClientDto.class);
        log.debug("actual: " + actual);

        HttpStatus statusCode = actual.getStatusCode();
        ClientDto body = actual.getBody();

        assertThat(statusCode).isEqualTo(HttpStatus.CREATED);
        assertThat(body).isNotNull();
        assertThat(body.getId()).isGreaterThan(0L);
        assertThat(body.getLastname()).isEqualTo(clientDto.getLastname());
        assertThat(body.getFirstname()).isEqualTo(clientDto.getFirstname());
        assertThat(body.getMiddlename()).isEqualTo(clientDto.getMiddlename());
        assertThat(body.getAge()).isEqualTo(clientDto.getAge());
        assertThat(body.getAccounts()).isEqualTo(clientDto.getAccounts());
    }

    @Test
    @DisplayName("[TestRestTemplate] Успешное обновление клиента")
    public void updateSuccess() {
        Long id = saveClient().getId();

        ClientDto newClientDto = new ClientDto("New_last", "New_first", "New_mid", 55);
        log.debug("newClientDto: " + newClientDto);

        String url = "http://localhost:" + port + BASE_URL + "/" + id;
        log.debug("url: " + url);

        HttpEntity<ClientDto> entity = new HttpEntity<>(newClientDto);

        ResponseEntity<ClientDto> actual = testRestTemplate.exchange(url, HttpMethod.PUT, entity, ClientDto.class, id);
        log.debug("actual: " + actual);

        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(actual.getBody()).isNull();
    }

    @Test
    @DisplayName("[TestRestTemplate] Успешное удаление клиента")
    public void deleteSuccess() {
        Long id = saveClient().getId();

        String url = "http://localhost:" + port + BASE_URL + "/" + id;
        log.debug("url: " + url);

        ResponseEntity<ClientDto> actual = testRestTemplate.exchange(url, HttpMethod.DELETE, null, ClientDto.class, id);
        log.debug("actual: " + actual);

        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(actual.getBody()).isNull();
    }
}
