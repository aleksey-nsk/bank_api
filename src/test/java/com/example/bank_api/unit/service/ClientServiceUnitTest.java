package com.example.bank_api.unit.service;

import com.example.bank_api.dto.ClientDto;
import com.example.bank_api.entity.Account;
import com.example.bank_api.entity.Client;
import com.example.bank_api.exception.ClientDuplicateException;
import com.example.bank_api.exception.ClientNotFoundException;
import com.example.bank_api.repository.AccountRepository;
import com.example.bank_api.repository.CardRepository;
import com.example.bank_api.repository.ClientRepository;
import com.example.bank_api.service.ClientService;
import com.example.bank_api.service.impl.ClientServiceImpl;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang.RandomStringUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

import static org.assertj.core.api.Assertions.assertThat;

// Опять ставим аннотацию @SpringBootTest,
// но теперь уже указываем в скобках classes.
//
// @SpringBootTest(classes = ClientServiceImpl.class) => в этом случае
// при запуске теста КОНТЕКСТ ПОДНИМЕТСЯ ТОЛЬКО С 1 КЛАССОМ.
//
// classes - это массив (можно несколько добавить).

@SpringBootTest(classes = ClientServiceImpl.class)
@Log4j2
@ActiveProfiles("test")
public class ClientServiceUnitTest {

    @Autowired
    private ClientService clientService;

    // Нужный бин можно ЗАМОКАТЬ
    @MockBean
    private ClientRepository clientRepository;

    @MockBean
    private AccountRepository accountRepository;

    @MockBean
    private CardRepository cardRepository;

    private Client createClient(Long id) {
        String last = RandomStringUtils.randomAlphabetic(10);
        String first = RandomStringUtils.randomAlphabetic(8);
        String mid = RandomStringUtils.randomAlphabetic(6);
        Integer age = ThreadLocalRandom.current().nextInt(18, 120);
        List<Account> accounts = Collections.emptyList();

        Client client = new Client(id, last, first, mid, age, accounts);
        log.debug("client: " + client);

        return client;
    }

    @Test
    @DisplayName("Успешный поиск всех клиентов")
    public void findAllSuccess() {
        ClientDto clientDto1 = ClientDto.valueOf(createClient(1L));
        ClientDto clientDto2 = ClientDto.valueOf(createClient(2L));
        List<ClientDto> clientDtoList = new ArrayList<>();
        clientDtoList.add(clientDto1);
        clientDtoList.add(clientDto2);
        log.debug("clientDtoList: " + clientDtoList);

        Client client1 = clientDto1.mapToClient();
        Client client2 = clientDto2.mapToClient();
        List<Client> clientList = new ArrayList<>();
        clientList.add(client1);
        clientList.add(client2);
        log.debug("clientList: " + clientList);

        // Далее надо определить поведение ЗАМОКАННОГО БИНА
        // (сейчас он просто возвращает null на любой вызов).
        //
        // Spring Boot имеет встроенную зависимость
        // для библиотеки Mockito
        Mockito
                .doReturn(clientList) // вернуть clientList
                .when(clientRepository).findAll(); // когда у ЗАМОКАННОГО ОБЪЕКТА вызывается метод findAll()

        List<ClientDto> actual = clientService.findAll();
        log.debug("actual: " + actual);

        assertThat(actual).size().isEqualTo(2);
        assertThat(actual).isEqualTo(clientDtoList);
    }

    @Test
    @DisplayName("Успешный поиск клиента по id")
    public void findByIdSuccess() {
        Long id = 1L;
        ClientDto clientDto = ClientDto.valueOf(createClient(id));
        Client client = clientDto.mapToClient();

        Mockito.doReturn(Optional.of(client))
                .when(clientRepository).findById(id);

        ClientDto actual = clientService.findById(id);

        assertThat(actual).isNotNull();
        assertThat(actual).isEqualTo(clientDto);
    }

    @Test
    @DisplayName("Клиент по id не найден")
    public void findByIdFail() {
        Long id = 1L;

        Mockito.doReturn(Optional.empty())
                .when(clientRepository).findById(id);

        try {
            clientService.findById(id);
        } catch (ClientNotFoundException e) {
            log.debug(e.getMessage());
            assertThat(e.getMessage()).isEqualTo("Не найден клиент по id=" + id);
        }
    }

    @Test
    @DisplayName("Успешное добавление клиента без счетов")
    public void saveSuccess() {
        ClientDto clientDto = ClientDto.valueOf(createClient(1L));

        Client client = clientDto.mapToClient();
        String last = client.getLastname();
        String first = client.getFirstname();
        String mid = client.getMiddlename();

        Mockito.when(clientRepository.findByLastnameAndFirstnameAndMiddlename(last, first, mid))
                .thenReturn(null);

        Mockito.when(clientRepository.save(client))
                .thenReturn(client);

        ClientDto actual = clientService.save(clientDto);

        assertThat(actual).isNotNull();
        assertThat(actual).isEqualTo(clientDto);
    }

    @Test
    @DisplayName("Дубликат клиента по ФИО не добавлен")
    public void saveFail() {
        ClientDto clientDto = ClientDto.valueOf(createClient(1L));

        Client client = clientDto.mapToClient();
        String last = client.getLastname();
        String first = client.getFirstname();
        String mid = client.getMiddlename();
        String fullName = last + " " + first + " " + mid;

        Mockito.when(clientRepository.findByLastnameAndFirstnameAndMiddlename(last, first, mid))
                .thenReturn(client);

        try {
            clientService.save(clientDto);
        } catch (ClientDuplicateException e) {
            log.debug(e.getMessage());
            assertThat(e.getMessage()).isEqualTo("В БД уже есть клиент с полным именем '" + fullName + "'");
        }
    }

    @Test
    @DisplayName("Клиент для обновления не найден")
    public void updateFail() {
        Long id = 1L;
        ClientDto clientDto = ClientDto.valueOf(createClient(id));

        try {
            clientService.update(id, clientDto);
        } catch (ClientNotFoundException e) {
            log.debug(e.getMessage());
            assertThat(e.getMessage()).isEqualTo("Не найден клиент по id=" + id);
        }
    }

    @Test
    @DisplayName("Клиент для удаления не найден")
    public void deleteFail() {
        Long id = 1L;

        try {
            clientService.delete(id);
        } catch (ClientNotFoundException e) {
            log.debug(e.getMessage());
            assertThat(e.getMessage()).isEqualTo("Не найден клиент по id=" + id);
        }
    }
}
