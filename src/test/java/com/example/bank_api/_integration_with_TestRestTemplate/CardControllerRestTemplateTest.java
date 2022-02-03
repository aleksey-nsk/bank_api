package com.example.bank_api._integration_with_TestRestTemplate;

import com.example.bank_api.dto.AccountDto;
import com.example.bank_api.dto.CardDto;
import com.example.bank_api.entity.Account;
import com.example.bank_api.entity.Card;
import com.example.bank_api.entity.Client;
import com.example.bank_api.repository.AccountRepository;
import com.example.bank_api.repository.CardRepository;
import com.example.bank_api.repository.ClientRepository;
import com.example.bank_api.service.AccountService;
import com.example.bank_api.service.CardService;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang.RandomStringUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@Log4j2
@ActiveProfiles("test")
public class CardControllerRestTemplateTest {

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private CardRepository cardRepository;

    @Autowired
    private AccountService accountService;

    @Autowired
    private CardService cardService;

    @Autowired
    private TestRestTemplate testRestTemplate;

    private static final String BASE_URL = "/api/v1/client/";

    @LocalServerPort
    private String port;

    @AfterEach
    void tearDown() {
        cardRepository.deleteAll();
        accountRepository.deleteAll();
        clientRepository.deleteAll();
    }

    private Client saveClientInDB() {
        String last = RandomStringUtils.randomAlphabetic(10);
        String first = RandomStringUtils.randomAlphabetic(8);
        String mid = RandomStringUtils.randomAlphabetic(6);
        Integer age = ThreadLocalRandom.current().nextInt(18, 120);
        List<Account> accounts = Collections.emptyList();

        Client client = new Client(last, first, mid, age, accounts);
        log.debug("client: " + client);

        Client savedClient = clientRepository.save(client);
        log.debug("savedClient: " + savedClient);

        return savedClient;
    }

    private Account saveAccountInDB(Client client) {
        AccountDto savedAccountDto = accountService.save(client.getId());
        return savedAccountDto.mapToAccount();
    }

    @Test
    @DisplayName("Успешное добавление карты по счёту")
    public void saveSuccess() {
        Client client = saveClientInDB();
        Account account = saveAccountInDB(client);

        assertThat(clientRepository.findAll()).size().isEqualTo(1);
        assertThat(accountRepository.findAll()).size().isEqualTo(1);
        assertThat(cardRepository.findAll()).size().isEqualTo(0);

        String url = "http://localhost:" + port + BASE_URL + client.getId() + "/account/" + account.getId() + "/card";
        log.debug("url: " + url);

        ResponseEntity<CardDto> actual = testRestTemplate.postForEntity(url, null, CardDto.class);
        log.debug("actual: " + actual);

        HttpStatus statusCode = actual.getStatusCode();
        CardDto body = actual.getBody();
        assertThat(statusCode).isEqualTo(HttpStatus.CREATED);
        assertThat(body).isNotNull();

        Long cardId = body.getId();
        assertThat(cardId).isGreaterThan(0L);

        Card cardFromDB = cardRepository.findById(cardId).get();
        assertThat(body.getCardNumber()).isEqualTo(cardFromDB.getCardNumber());
        assertThat(body.getReleaseDate()).isEqualTo(cardFromDB.getReleaseDate());

        assertThat(clientRepository.findAll()).size().isEqualTo(1);
        assertThat(accountRepository.findAll()).size().isEqualTo(1);
        assertThat(cardRepository.findAll()).size().isEqualTo(1);
    }

    @Test
    @DisplayName("Неуспешное добавление карты по счёту: клиент не найден")
    public void saveFailClient() {
        Client client = saveClientInDB();
        Account account = saveAccountInDB(client);

        assertThat(clientRepository.findAll()).size().isEqualTo(1);
        assertThat(accountRepository.findAll()).size().isEqualTo(1);
        assertThat(cardRepository.findAll()).size().isEqualTo(0);

        Long nonExistentClientId = client.getId() + 1L;
        log.debug("nonExistentClientId: " + nonExistentClientId);

        String url = "http://localhost:" + port + BASE_URL + nonExistentClientId + "/account/" + account.getId() + "/card";
        log.debug("url: " + url);

        ResponseEntity<Object> actual = testRestTemplate.postForEntity(url, null, Object.class);
        log.debug("actual: " + actual);

        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

        assertThat(clientRepository.findAll()).size().isEqualTo(1);
        assertThat(accountRepository.findAll()).size().isEqualTo(1);
        assertThat(cardRepository.findAll()).size().isEqualTo(0);
    }

    @Test
    @DisplayName("Неуспешное добавление карты по счёту: счёт не найден")
    public void saveFailAccount() {
        Client client = saveClientInDB();
        Account account = saveAccountInDB(client);

        assertThat(clientRepository.findAll()).size().isEqualTo(1);
        assertThat(accountRepository.findAll()).size().isEqualTo(1);
        assertThat(cardRepository.findAll()).size().isEqualTo(0);

        Long nonExistentAccountId = account.getId() + 1L;
        log.debug("nonExistentAccountId: " + nonExistentAccountId);

        String url = "http://localhost:" + port + BASE_URL + client.getId() + "/account/" + nonExistentAccountId + "/card";
        log.debug("url: " + url);

        ResponseEntity<Object> actual = testRestTemplate.postForEntity(url, null, Object.class);
        log.debug("actual: " + actual);

        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

        assertThat(clientRepository.findAll()).size().isEqualTo(1);
        assertThat(accountRepository.findAll()).size().isEqualTo(1);
        assertThat(cardRepository.findAll()).size().isEqualTo(0);
    }

    @Test
    @DisplayName("Успешное удаление карты")
    public void deleteSuccess() {
        Client client = saveClientInDB();
        Account account = saveAccountInDB(client);
        Card card = cardService.save(client.getId(), account.getId()).mapToCard();

        assertThat(clientRepository.findAll()).size().isEqualTo(1);
        assertThat(accountRepository.findAll()).size().isEqualTo(1);
        assertThat(cardRepository.findAll()).size().isEqualTo(1);

        String url = "http://localhost:" + port + BASE_URL + client.getId() + "/card/" + card.getId();
        log.debug("url: " + url);

        ResponseEntity<Object> actual = testRestTemplate.exchange(url, HttpMethod.DELETE, null, Object.class);
        log.debug("actual: " + actual);

        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(actual.getBody()).isNull();

        assertThat(clientRepository.findAll()).size().isEqualTo(1);
        assertThat(accountRepository.findAll()).size().isEqualTo(1);
        assertThat(cardRepository.findAll()).size().isEqualTo(0);
    }

    @Test
    @DisplayName("Неуспешное удаление карты: клиент не найден")
    public void deleteFailClient() {
        Client client = saveClientInDB();
        Account account = saveAccountInDB(client);
        Card card = cardService.save(client.getId(), account.getId()).mapToCard();

        assertThat(clientRepository.findAll()).size().isEqualTo(1);
        assertThat(accountRepository.findAll()).size().isEqualTo(1);
        assertThat(cardRepository.findAll()).size().isEqualTo(1);

        Long nonExistentClientId = client.getId() + 1L;
        log.debug("nonExistentClientId: " + nonExistentClientId);

        String url = "http://localhost:" + port + BASE_URL + nonExistentClientId + "/card/" + card.getId();
        log.debug("url: " + url);

        ResponseEntity<Object> actual = testRestTemplate.exchange(url, HttpMethod.DELETE, null, Object.class);
        log.debug("actual: " + actual);

        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

        assertThat(clientRepository.findAll()).size().isEqualTo(1);
        assertThat(accountRepository.findAll()).size().isEqualTo(1);
        assertThat(cardRepository.findAll()).size().isEqualTo(1);
    }

    @Test
    @DisplayName("Неуспешное удаление карты: карта не найдена")
    public void deleteFailCard() {
        Client client = saveClientInDB();
        Account account = saveAccountInDB(client);
        Card card = cardService.save(client.getId(), account.getId()).mapToCard();

        assertThat(clientRepository.findAll()).size().isEqualTo(1);
        assertThat(accountRepository.findAll()).size().isEqualTo(1);
        assertThat(cardRepository.findAll()).size().isEqualTo(1);

        Long nonExistentCardId = card.getId() + 1L;
        log.debug("nonExistentCardId: " + nonExistentCardId);

        String url = "http://localhost:" + port + BASE_URL + client.getId() + "/card/" + nonExistentCardId;
        log.debug("url: " + url);

        ResponseEntity<Object> actual = testRestTemplate.exchange(url, HttpMethod.DELETE, null, Object.class);
        log.debug("actual: " + actual);

        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

        assertThat(clientRepository.findAll()).size().isEqualTo(1);
        assertThat(accountRepository.findAll()).size().isEqualTo(1);
        assertThat(cardRepository.findAll()).size().isEqualTo(1);
    }
}
