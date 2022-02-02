package com.example.bank_api._integration_with_TestRestTemplate;

import com.example.bank_api.dto.CardDto;
import com.example.bank_api.dto.ClientDto;
import com.example.bank_api.entity.Account;
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
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

// Чтобы запустить тесты через Spring Boot (используя контекст Spring-а)
// надо написать аннотацию @SpringBootTest.
//
// Аннотация @SpringBootTest содержит в себе
// @ExtendWith, @BootstrapWith (поднимает весь контекст целиком).
//
// RANDOM_PORT - значит сервер Tomcat будет запускаться на рандомном порту, независимо от настроек.
// Чтобы получить порт используем @LocalServerPort.

@SpringBootTest(webEnvironment = RANDOM_PORT)
@Log4j2
@ActiveProfiles("test")
public class ClientControllerRestTemplateTest {

    // Навесил на класс @SpringBootTest => теперь
    // в классе могу заавтоварить любой бин
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

    // Класс TestRestTemplate позволяет писать тесты для REST API.
    // Здесь мы запускаем вэб-сервер, делаем запросы - всё по-настоящему.
    // Тестируем контроллеры.
    // С TestRestTemplate мы по-настоящему запускаем сервер
    @Autowired
    private TestRestTemplate testRestTemplate;

    // Получить порт
    @LocalServerPort
    private String port;

    private static final String BASE_URL = "/api/v1/client";

    // Очищаю БД после каждого теста
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

    private Client createClient() {
        String last = RandomStringUtils.randomAlphabetic(10);
        String first = RandomStringUtils.randomAlphabetic(8);
        String mid = RandomStringUtils.randomAlphabetic(6);
        Integer age = ThreadLocalRandom.current().nextInt(18, 120);
        List<Account> accounts = Collections.emptyList();

        Client client = new Client(last, first, mid, age, accounts);
        log.debug("client: " + client);

        return client;
    }

    @Test
    @DisplayName("Успешный поиск всех клиентов")
    public void findAllSuccess() {
        Client saved1 = saveClientInDB();
        Client saved2 = saveClientInDB();

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
        assertThat(body).size().isEqualTo(2);

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
    @DisplayName("Успешный поиск клиента по id")
    public void findByIdSuccess() {
        Client saved = saveClientInDB();

        String url = "http://localhost:" + port + BASE_URL + "/" + saved.getId();
        log.debug("url: " + url);

        // testRestTemplate.getForObject() - возвращает ТЕЛО ОТВЕТА (если статус не 200, то кинет ошибку).
        // testRestTemplate.getForEntity() - возвращает ОБЁРТКУ (есть СТАТУС ОТВЕТА и само ТЕЛО ОТВЕТА)
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
    @DisplayName("Клиент по id не найден")
    public void findByIdFail() {
        String url = "http://localhost:" + port + BASE_URL + "/1";
        log.debug("url: " + url);

        ResponseEntity<ClientDto> actual = testRestTemplate.getForEntity(url, ClientDto.class);
        log.debug("actual: " + actual);

        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("Успешное добавление клиента без счетов")
    public void saveSuccess() {
        Client client = createClient();
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

        assertThat(clientRepository.findAll()).size().isEqualTo(1);
        assertThat(accountRepository.findAll()).size().isEqualTo(0);
        assertThat(cardRepository.findAll()).size().isEqualTo(0);
    }

    @Test
    @DisplayName("Дубликат клиента по ФИО в БД не добавлен")
    public void saveFail() {
        Client saved = saveClientInDB();

        // Создаём дубликат по ФИО
        Client duplicate = new Client(
                saved.getLastname(),
                saved.getFirstname(),
                saved.getMiddlename(),
                ThreadLocalRandom.current().nextInt(18, 120),
                Collections.emptyList()
        );
        ClientDto duplicateDto = ClientDto.valueOf(duplicate);
        log.debug("duplicateDto: " + duplicateDto);

        String url = "http://localhost:" + port + BASE_URL;
        log.debug("url: " + url);

        ResponseEntity<ClientDto> actual = testRestTemplate.postForEntity(url, duplicateDto, ClientDto.class);
        log.debug("actual: " + actual);

        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);

        assertThat(clientRepository.findAll()).size().isEqualTo(1);
        assertThat(accountRepository.findAll()).size().isEqualTo(0);
        assertThat(cardRepository.findAll()).size().isEqualTo(0);
    }

    @Test
    @DisplayName("Успешное обновление клиента")
    public void updateSuccess() {
        Long id = saveClientInDB().getId();
        Client update = createClient();
        ClientDto updateDto = ClientDto.valueOf(update);
        log.debug("updateDto: " + updateDto);

        String url = "http://localhost:" + port + BASE_URL + "/" + id;
        log.debug("url: " + url);

        HttpEntity<ClientDto> httpEntity = new HttpEntity<>(updateDto);
        ResponseEntity<ClientDto> actual = testRestTemplate.exchange(url, HttpMethod.PUT, httpEntity, ClientDto.class, id);
        log.debug("actual: " + actual);

        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(actual.getBody()).isNull();

        assertThat(clientRepository.findAll()).size().isEqualTo(1);
        assertThat(accountRepository.findAll()).size().isEqualTo(0);
        assertThat(cardRepository.findAll()).size().isEqualTo(0);

        Client updated = clientRepository.findById(id).get();

        assertThat(updated.getLastname()).isEqualTo(updateDto.getLastname());
        assertThat(updated.getFirstname()).isEqualTo(updateDto.getFirstname());
        assertThat(updated.getMiddlename()).isEqualTo(updateDto.getMiddlename());
        assertThat(updated.getAge()).isEqualTo(updateDto.getAge());
    }

    @Test
    @DisplayName("Клиент для обновления не найден")
    public void updateFail() {
        Long id = 1L;
        Client update = createClient();
        ClientDto updateDto = ClientDto.valueOf(update);
        log.debug("updateDto: " + updateDto);

        String url = "http://localhost:" + port + BASE_URL + "/" + id;
        log.debug("url: " + url);

        HttpEntity<ClientDto> httpEntity = new HttpEntity<>(updateDto);
        ResponseEntity<ClientDto> actual = testRestTemplate.exchange(url, HttpMethod.PUT, httpEntity, ClientDto.class, id);
        log.debug("actual: " + actual);

        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

        assertThat(clientRepository.findAll()).size().isEqualTo(0);
        assertThat(accountRepository.findAll()).size().isEqualTo(0);
        assertThat(cardRepository.findAll()).size().isEqualTo(0);
    }

    @Test
    @DisplayName("Успешное удаление клиента без счетов")
    public void deleteSuccess() {
        Long id = saveClientInDB().getId();

        String url = "http://localhost:" + port + BASE_URL + "/" + id;
        log.debug("url: " + url);

        ResponseEntity<Object> actual = testRestTemplate.exchange(url, HttpMethod.DELETE, null, Object.class, id);
        log.debug("actual: " + actual);

        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(actual.getBody()).isNull();

        assertThat(clientRepository.findAll()).size().isEqualTo(0);
        assertThat(accountRepository.findAll()).size().isEqualTo(0);
        assertThat(cardRepository.findAll()).size().isEqualTo(0);
    }

    @Test
    @DisplayName("Успешное удаление клиента со счетами и картами")
    public void deleteWithAccountsAndCardsSuccess() {
        Long clientId = saveClientInDB().getId();

        Long accountId1 = accountService.save(clientId).getId();
        Long accountId2 = accountService.save(clientId).getId();

        CardDto card11 = cardService.save(clientId, accountId1);
        CardDto card12 = cardService.save(clientId, accountId1);
        CardDto card21 = cardService.save(clientId, accountId2);
        CardDto card22 = cardService.save(clientId, accountId2);

        assertThat(clientRepository.findAll()).size().isEqualTo(1);
        assertThat(accountRepository.findAll()).size().isEqualTo(2);
        assertThat(cardRepository.findAll()).size().isEqualTo(4);

        String url = "http://localhost:" + port + BASE_URL + "/" + clientId;
        log.debug("url: " + url);

        ResponseEntity<Object> actual = testRestTemplate.exchange(url, HttpMethod.DELETE, null, Object.class, clientId);
        log.debug("actual: " + actual);

        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(actual.getBody()).isNull();

        assertThat(clientRepository.findAll()).size().isEqualTo(0);
        assertThat(accountRepository.findAll()).size().isEqualTo(0);
        assertThat(cardRepository.findAll()).size().isEqualTo(0);
    }

    @Test
    @DisplayName("Клиент для удаления не найден")
    public void deleteFail() {
        Long id = 1L;

        String url = "http://localhost:" + port + BASE_URL + "/" + id;
        log.debug("url: " + url);

        ResponseEntity<Object> actual = testRestTemplate.exchange(url, HttpMethod.DELETE, null, Object.class, id);
        log.debug("actual: " + actual);

        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

        assertThat(clientRepository.findAll()).size().isEqualTo(0);
        assertThat(accountRepository.findAll()).size().isEqualTo(0);
        assertThat(cardRepository.findAll()).size().isEqualTo(0);
    }
}
