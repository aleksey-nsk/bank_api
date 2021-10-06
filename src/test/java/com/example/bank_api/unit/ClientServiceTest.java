package com.example.bank_api.unit;

import com.example.bank_api.dto.ClientDto;
import com.example.bank_api.entity.Account;
import com.example.bank_api.entity.Client;
import com.example.bank_api.repository.ClientRepository;
import com.example.bank_api.service.ClientService;
import com.example.bank_api.service.impl.ClientServiceImpl;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang.RandomStringUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@SpringBootTest(classes = ClientServiceImpl.class)
@Log4j2
public class ClientServiceTest {

    @Autowired
    private ClientService clientService;

    @MockBean
    private ClientRepository clientRepository;

    private ClientDto createClient(Long id) {
        String last = RandomStringUtils.randomAlphabetic(10);
        String first = RandomStringUtils.randomAlphabetic(8);
        String mid = RandomStringUtils.randomAlphabetic(6);
        Integer age = 41;
        List<Account> accounts = Collections.emptyList();

        Client client = new Client(id, last, first, mid, age, accounts);
        log.debug("client: " + client);

        ClientDto clientDto = ClientDto.valueOf(client);
        log.debug("clientDto: " + clientDto);

        return clientDto;
    }

    @Test
    @DisplayName("[Service] Успешный поиск всех клиентов")
    public void findAllSuccess() {
        ClientDto clientDto1 = createClient(1L);
        ClientDto clientDto2 = createClient(2L);

        Client client1 = clientDto1.mapToClient();
        Client client2 = clientDto2.mapToClient();

        List<ClientDto> clientDtoList = new ArrayList<>();
        clientDtoList.add(clientDto1);
        clientDtoList.add(clientDto2);
        log.debug("clientDtoList: " + clientDtoList);

        List<Client> clientList = new ArrayList<>();
        clientList.add(client1);
        clientList.add(client2);
        log.debug("clientList: " + clientList);

        Mockito.doReturn(clientList)
                .when(clientRepository).findAll();

        List<ClientDto> actual = clientService.findAll();
        log.debug("actual: " + actual);

        Assertions.assertThat(actual).isNotEmpty();
        Assertions.assertThat(actual).isEqualTo(clientDtoList);
    }

    @Test
    @DisplayName("[Service] Успешный поиск клиента по id")
    public void findByIdSuccess() {
        Long id = 1L;
        ClientDto clientDto = createClient(id);
        Client client = clientDto.mapToClient();

        Mockito.doReturn(Optional.of(client))
                .when(clientRepository).findById(id);

        ClientDto actual = clientService.findById(id);
        log.debug("actual: " + actual);

        Assertions.assertThat(actual).isNotNull();
        Assertions.assertThat(actual).isEqualTo(clientDto);
    }

    @Test
    @DisplayName("[Service] Клиент по id не найден")
    public void findByIdFail() {
        Long id = 1L;

        Mockito.doReturn(Optional.empty())
                .when(clientRepository).findById(id);

        ClientDto actual = clientService.findById(id);
        log.debug("actual: " + actual);

        Assertions.assertThat(actual).isNull();
    }

    @Test
    @DisplayName("[Service] Успешное добавление клиента")
    public void saveSuccess() {
        ClientDto clientDto = createClient(1L);
        Client client = clientDto.mapToClient();

        Mockito.when(clientRepository.findByLastnameAndFirstnameAndMiddlename(
                clientDto.getLastname(),
                clientDto.getFirstname(),
                clientDto.getMiddlename())
        )
                .thenReturn(null);

        Mockito.when(clientRepository.save(client))
                .thenReturn(client);

        ClientDto actual = clientService.save(clientDto);
        log.debug("actual: " + actual);

        Assertions.assertThat(actual).isNotNull();
        Assertions.assertThat(actual).isEqualTo(clientDto);
    }

    @Test
    @DisplayName("[Service] Дубликат клиента не сохранён")
    public void saveFail() {
        ClientDto clientDto = createClient(1L);

        Mockito.when(clientRepository.findByLastnameAndFirstnameAndMiddlename(
                clientDto.getLastname(),
                clientDto.getFirstname(),
                clientDto.getMiddlename())
        )
                .thenReturn(new Client());

        ClientDto actual = clientService.save(clientDto);
        log.debug("actual: " + actual);

        Assertions.assertThat(actual).isNull();
    }
}
