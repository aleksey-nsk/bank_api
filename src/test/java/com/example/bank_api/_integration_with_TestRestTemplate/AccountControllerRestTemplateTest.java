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
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@Log4j2
@ActiveProfiles("test")
public class AccountControllerRestTemplateTest {

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

    @LocalServerPort
    private String port;

    private static final String BASE_URL = "/api/v1/client/";

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

    private Card saveCardInDB(Client client, Account account) {
        CardDto savedCardDto = cardService.save(client.getId(), account.getId());
        return savedCardDto.mapToCard();
    }

    @Test
    @DisplayName("Успешный поиск всех счетов клиента")
    public void findAllSuccess() {
        Client client = saveClientInDB();
        Account account1 = saveAccountInDB(client);
        Account account2 = saveAccountInDB(client);

        assertThat(clientRepository.findAll()).size().isEqualTo(1);
        assertThat(accountRepository.findAll()).size().isEqualTo(2);
        assertThat(cardRepository.findAll()).size().isEqualTo(0);

        String url = "http://localhost:" + port + BASE_URL + client.getId() + "/account";
        log.debug("url: " + url);

        ResponseEntity<List<AccountDto>> actual = testRestTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<AccountDto>>() {
                }
        );
        log.debug("actual: " + actual);

        HttpStatus statusCode = actual.getStatusCode();
        List<AccountDto> body = actual.getBody();
        AccountDto actual1 = body.get(0);
        AccountDto actual2 = body.get(1);

        assertThat(statusCode).isEqualTo(HttpStatus.OK);
        assertThat(body).size().isEqualTo(2);

        assertThat(actual1.getId()).isEqualTo(account1.getId());
        assertThat(actual1.getNumber()).isEqualTo(account1.getNumber());
        assertThat(actual1.getOpeningDate()).isEqualTo(account1.getOpeningDate());
        assertThat(actual1.getBalance().compareTo(account1.getBalance())).isEqualTo(0);
        assertThat(actual1.getCards()).isEqualTo(account1.getCards());

        assertThat(actual2.getId()).isEqualTo(account2.getId());
        assertThat(actual2.getNumber()).isEqualTo(account2.getNumber());
        assertThat(actual2.getOpeningDate()).isEqualTo(account2.getOpeningDate());
        assertThat(actual2.getBalance().compareTo(account2.getBalance())).isEqualTo(0);
        assertThat(actual2.getCards()).isEqualTo(account2.getCards());
    }

    @Test
    @DisplayName("Неуспешный поиск всех счетов клиента: клиент не найден")
    public void findAllFail() {
        Client client = saveClientInDB();
        Account account1 = saveAccountInDB(client);
        Account account2 = saveAccountInDB(client);

        Long nonExistentClientId = client.getId() + 1L;
        log.debug("nonExistentClientId: " + nonExistentClientId);

        assertThat(clientRepository.findAll()).size().isEqualTo(1);
        assertThat(accountRepository.findAll()).size().isEqualTo(2);
        assertThat(cardRepository.findAll()).size().isEqualTo(0);

        String url = "http://localhost:" + port + BASE_URL + nonExistentClientId + "/account";
        log.debug("url: " + url);

        ResponseEntity<Object> actual = testRestTemplate.getForEntity(url, Object.class);
        log.debug("actual: " + actual);

        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("Успешный поиск счёта по id")
    public void findByIdSuccess() {
        Client client = saveClientInDB();
        Account account1 = saveAccountInDB(client);
        Account account2 = saveAccountInDB(client);

        assertThat(clientRepository.findAll()).size().isEqualTo(1);
        assertThat(accountRepository.findAll()).size().isEqualTo(2);
        assertThat(cardRepository.findAll()).size().isEqualTo(0);

        String url = "http://localhost:" + port + BASE_URL + client.getId() + "/account/" + account2.getId();
        log.debug("url: " + url);

        ResponseEntity<AccountDto> actual = testRestTemplate.getForEntity(url, AccountDto.class);
        log.debug("actual: " + actual);

        HttpStatus statusCode = actual.getStatusCode();
        AccountDto body = actual.getBody();

        assertThat(statusCode).isEqualTo(HttpStatus.OK);
        assertThat(body).isNotNull();

        assertThat(body.getId()).isEqualTo(account2.getId());
        assertThat(body.getNumber()).isEqualTo(account2.getNumber());
        assertThat(body.getOpeningDate()).isEqualTo(account2.getOpeningDate());
        assertThat(body.getBalance().compareTo(account2.getBalance())).isEqualTo(0);
        assertThat(body.getCards()).isEqualTo(account2.getCards());
    }

    @Test
    @DisplayName("Счёт по id не найден: клиент не найден")
    public void findByIdFailClient() {
        Client client = saveClientInDB();
        Account account = saveAccountInDB(client);

        Long nonExistentClientId = client.getId() + 1L;
        log.debug("nonExistentClientId: " + nonExistentClientId);

        assertThat(clientRepository.findAll()).size().isEqualTo(1);
        assertThat(accountRepository.findAll()).size().isEqualTo(1);
        assertThat(cardRepository.findAll()).size().isEqualTo(0);

        String url = "http://localhost:" + port + BASE_URL + nonExistentClientId + "/account/" + account.getId();
        log.debug("url: " + url);

        ResponseEntity<Object> actual = testRestTemplate.getForEntity(url, Object.class);
        log.debug("actual: " + actual);

        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("Счёт по id не найден: счёт не найден")
    public void findByIdFailAccount() {
        Client client1 = saveClientInDB();
        Account account1 = saveAccountInDB(client1);

        Client client2 = saveClientInDB();
        Account account2 = saveAccountInDB(client2);

        assertThat(clientRepository.findAll()).size().isEqualTo(2);
        assertThat(accountRepository.findAll()).size().isEqualTo(2);
        assertThat(cardRepository.findAll()).size().isEqualTo(0);

        String url = "http://localhost:" + port + BASE_URL + client2.getId() + "/account/" + account1.getId();
        log.debug("url: " + url);

        ResponseEntity<AccountDto> actual = testRestTemplate.getForEntity(url, AccountDto.class);
        log.debug("actual: " + actual);

        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("Успешное добавление счёта клиенту (без карт)")
    public void saveSuccess() {
        Long clientId = saveClientInDB().getId();
        assertThat(clientRepository.findAll()).size().isEqualTo(1);
        assertThat(accountRepository.findAll()).size().isEqualTo(0);
        assertThat(cardRepository.findAll()).size().isEqualTo(0);

        String url = "http://localhost:" + port + BASE_URL + clientId + "/account";
        log.debug("url: " + url);

        ResponseEntity<AccountDto> actual = testRestTemplate.postForEntity(url, null, AccountDto.class);
        log.debug("actual: " + actual);

        HttpStatus statusCode = actual.getStatusCode();
        AccountDto body = actual.getBody();
        assertThat(statusCode).isEqualTo(HttpStatus.CREATED);
        assertThat(body).isNotNull();

        Long accountId = body.getId();
        assertThat(accountId).isGreaterThan(0L);

        Account accountFromDB = accountRepository.findById(accountId).get();
        assertThat(body.getNumber()).isEqualTo(accountFromDB.getNumber());
        assertThat(body.getOpeningDate()).isEqualTo(accountFromDB.getOpeningDate());
        assertThat(body.getBalance().compareTo(accountFromDB.getBalance())).isEqualTo(0);
        assertThat(body.getCards()).isEqualTo(Collections.emptyList());

        assertThat(clientRepository.findAll()).size().isEqualTo(1);
        assertThat(accountRepository.findAll()).size().isEqualTo(1);
        assertThat(cardRepository.findAll()).size().isEqualTo(0);
    }

    @Test
    @DisplayName("Неуспешное добавление счёта клиенту: клиент не найден")
    public void saveFail() {
        Client client = saveClientInDB();

        assertThat(clientRepository.findAll()).size().isEqualTo(1);
        assertThat(accountRepository.findAll()).size().isEqualTo(0);
        assertThat(cardRepository.findAll()).size().isEqualTo(0);

        Long nonExistentClientId = client.getId() + 1L;
        log.debug("nonExistentClientId: " + nonExistentClientId);

        String url = "http://localhost:" + port + BASE_URL + nonExistentClientId + "/account";
        log.debug("url: " + url);

        ResponseEntity<Object> actual = testRestTemplate.postForEntity(url, null, Object.class);
        log.debug("actual: " + actual);

        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

        assertThat(clientRepository.findAll()).size().isEqualTo(1);
        assertThat(accountRepository.findAll()).size().isEqualTo(0);
        assertThat(cardRepository.findAll()).size().isEqualTo(0);
    }

    @Test
    @DisplayName("Успешное обновление счёта: добавлены деньги к текущему балансу")
    public void updateSuccess() {
        Client client = saveClientInDB();
        Account account = saveAccountInDB(client);
        Card card = saveCardInDB(client, account);

        assertThat(clientRepository.findAll()).size().isEqualTo(1);
        assertThat(accountRepository.findAll()).size().isEqualTo(1);
        assertThat(cardRepository.findAll()).size().isEqualTo(1);

        Long val = ThreadLocalRandom.current().nextLong(10, 100);
        BigDecimal add = BigDecimal.valueOf(val);
        log.debug("add: " + add);

        String url = "http://localhost:" + port + BASE_URL + client.getId() + "/account/card?cardNumber=" + card.getCardNumber() + "&add=" + add;
        log.debug("url: " + url);

        ResponseEntity<Object> actual = testRestTemplate.exchange(url, HttpMethod.PUT, null, Object.class);
        log.debug("actual: " + actual);

        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(actual.getBody()).isNull();

        assertThat(clientRepository.findAll()).size().isEqualTo(1);
        assertThat(accountRepository.findAll()).size().isEqualTo(1);
        assertThat(cardRepository.findAll()).size().isEqualTo(1);

        Account updated = accountRepository.findById(account.getId()).get();

        assertThat(updated.getId()).isEqualTo(account.getId());
        assertThat(updated.getNumber()).isEqualTo(account.getNumber());
        assertThat(updated.getOpeningDate().getTime()).isEqualTo(account.getOpeningDate().getTime());
        assertThat(updated.getBalance().compareTo(account.getBalance().add(add))).isEqualTo(0);
    }

    @Test
    @DisplayName("Неуспешное обновление счёта: клиент не найден")
    public void updateFailClient() {
        Client client = saveClientInDB();
        Account account = saveAccountInDB(client);
        Card card = saveCardInDB(client, account);

        assertThat(clientRepository.findAll()).size().isEqualTo(1);
        assertThat(accountRepository.findAll()).size().isEqualTo(1);
        assertThat(cardRepository.findAll()).size().isEqualTo(1);

        Long nonExistentClientId = client.getId() + 1L;
        log.debug("nonExistentClientId: " + nonExistentClientId);

        Long val = ThreadLocalRandom.current().nextLong(10, 100);
        BigDecimal add = BigDecimal.valueOf(val);
        log.debug("add: " + add);

        String url = "http://localhost:" + port + BASE_URL + nonExistentClientId + "/account/card?cardNumber=" + card.getCardNumber() + "&add=" + add;
        log.debug("url: " + url);

        ResponseEntity<Object> actual = testRestTemplate.exchange(url, HttpMethod.PUT, null, Object.class);
        log.debug("actual: " + actual);

        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(clientRepository.findAll()).size().isEqualTo(1);
        assertThat(accountRepository.findAll()).size().isEqualTo(1);
        assertThat(cardRepository.findAll()).size().isEqualTo(1);

        Account notUpdated = accountRepository.findById(account.getId()).get();

        assertThat(notUpdated.getId()).isEqualTo(account.getId());
        assertThat(notUpdated.getNumber()).isEqualTo(account.getNumber());
        assertThat(notUpdated.getOpeningDate().getTime()).isEqualTo(account.getOpeningDate().getTime());
        assertThat(notUpdated.getBalance().compareTo(account.getBalance())).isEqualTo(0);
    }

    @Test
    @DisplayName("Неуспешное обновление счёта: карта не найдена")
    public void updateFailCard() {
        Client client1 = saveClientInDB();
        Account account1 = saveAccountInDB(client1);
        Card card1 = saveCardInDB(client1, account1);

        Client client2 = saveClientInDB();
        Account account2 = saveAccountInDB(client2);
        Card card2 = saveCardInDB(client2, account2);

        assertThat(clientRepository.findAll()).size().isEqualTo(2);
        assertThat(accountRepository.findAll()).size().isEqualTo(2);
        assertThat(cardRepository.findAll()).size().isEqualTo(2);

        Long val = ThreadLocalRandom.current().nextLong(10, 100);
        BigDecimal add = BigDecimal.valueOf(val);
        log.debug("add: " + add);

        String url = "http://localhost:" + port + BASE_URL + client1.getId() + "/account/card?cardNumber=" + card2.getCardNumber() + "&add=" + add;
        log.debug("url: " + url);

        ResponseEntity<Object> actual = testRestTemplate.exchange(url, HttpMethod.PUT, null, Object.class);
        log.debug("actual: " + actual);

        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(clientRepository.findAll()).size().isEqualTo(2);
        assertThat(accountRepository.findAll()).size().isEqualTo(2);
        assertThat(cardRepository.findAll()).size().isEqualTo(2);

        Account notUpdated1 = accountRepository.findById(account1.getId()).get();
        assertThat(notUpdated1.getId()).isEqualTo(account1.getId());
        assertThat(notUpdated1.getNumber()).isEqualTo(account1.getNumber());
        assertThat(notUpdated1.getOpeningDate().getTime()).isEqualTo(account1.getOpeningDate().getTime());
        assertThat(notUpdated1.getBalance().compareTo(account1.getBalance())).isEqualTo(0);

        Account notUpdated2 = accountRepository.findById(account2.getId()).get();
        assertThat(notUpdated2.getId()).isEqualTo(account2.getId());
        assertThat(notUpdated2.getNumber()).isEqualTo(account2.getNumber());
        assertThat(notUpdated2.getOpeningDate().getTime()).isEqualTo(account2.getOpeningDate().getTime());
        assertThat(notUpdated2.getBalance().compareTo(account2.getBalance())).isEqualTo(0);
    }
}
