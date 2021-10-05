package com.example.bank_api._integration_old_style;

import com.example.bank_api.dto.ClientDto;
import com.example.bank_api.entity.Client;
import com.example.bank_api.repository.ClientRepository;
import lombok.extern.log4j.Log4j2;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@Log4j2
public class ClientControllerIntegrationTest {

    private static final String BASE_URL = "/api/v1/client";

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private TestRestTemplate testRestTemplate;

    @LocalServerPort
    private String port;

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
    public void findByIdSuccess() {
        Client client = new Client("Last", "First", "Mid", 22);
        log.debug("client: " + client);

        Client saved = clientRepository.save(client);
        log.debug("saved: " + saved);

        String url = "http://localhost:" + port + BASE_URL + "/" + saved.getId();
        log.debug("url: " + url);

        ResponseEntity<ClientDto> actual = testRestTemplate.getForEntity(url, ClientDto.class);
        log.debug("actual: " + actual);

        HttpStatus statusCode = actual.getStatusCode();
        log.debug("statusCode: " + statusCode);

        ClientDto body = actual.getBody();
        log.debug("body: " + body);

        Assertions.assertThat(statusCode)
                .isEqualTo(HttpStatus.OK);

        Assertions.assertThat(body)
                .isNotNull();
    }

    @Test
    public void findByIdFail() {
        String url = "http://localhost:" + port + BASE_URL + "/1";
        log.debug("url: " + url);

        ResponseEntity<ClientDto> actual = testRestTemplate.getForEntity(url, ClientDto.class);
        log.debug("actual: " + actual);

        HttpStatus statusCode = actual.getStatusCode();
        log.debug("statusCode: " + statusCode);

        ClientDto body = actual.getBody();
        log.debug("body: " + body);

        Assertions.assertThat(statusCode)
                .isEqualTo(HttpStatus.NOT_FOUND);
    }
}
