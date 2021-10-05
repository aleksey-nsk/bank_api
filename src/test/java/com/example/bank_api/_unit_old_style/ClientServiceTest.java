package com.example.bank_api._unit_old_style;

import com.example.bank_api.dto.ClientDto;
import com.example.bank_api.entity.Client;
import com.example.bank_api.repository.ClientRepository;
import com.example.bank_api.service.ClientService;
import com.example.bank_api.service.impl.ClientServiceImpl;
import lombok.extern.log4j.Log4j2;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@SpringBootTest(classes = ClientServiceImpl.class)
@Log4j2
public class ClientServiceTest {

    @Autowired
    private ClientService clientService;

    @MockBean
    private ClientRepository clientRepository;

    @Test
    public void findAllSuccess() {
        Client client = new Client(1L, "Last", "First", "Mid", 22, Collections.emptyList());
        List<Client> clientList = new ArrayList<>();
        clientList.add(client);
        log.debug("clientList: " + clientList);

        List<ClientDto> clientDtoList = clientList.stream()
                .map(it -> ClientDto.valueOf(it))
                .collect(Collectors.toList());
        log.debug("clientDtoList: " + clientDtoList);

        Mockito.doReturn(clientList)
                .when(clientRepository).findAll();

        List<ClientDto> actual = clientService.findAll();
        log.debug("actual: " + actual);

        Assertions.assertThat(actual)
                .isNotEmpty();

        Assertions.assertThat(actual)
                .isEqualTo(clientDtoList);
    }

    @Test
    public void findByIdSuccess() {
        Long id = 1L;

        Client client = new Client(id, "Last", "First", "Mid", 22, Collections.emptyList());
        log.debug("client: " + client);

        Mockito.doReturn(Optional.of(client))
                .when(clientRepository).findById(id);

        ClientDto clientDto = clientService.findById(id);
        log.debug("clientDto: " + clientDto);

        Assertions.assertThat(clientDto)
                .isNotNull();

        Assertions.assertThat(clientDto)
                .isEqualTo(clientDto);
    }

    @Test
    public void findByIdReturnNull() {
        Long id = 1L;

        Mockito.doReturn(Optional.empty())
                .when(clientRepository).findById(id);

        ClientDto clientDto = clientService.findById(id);
        log.debug("clientDto: " + clientDto);

        Assertions.assertThat(clientDto)
                .isNull();
    }
}
