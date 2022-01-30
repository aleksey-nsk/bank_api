package com.example.bank_api.service.impl;

import com.example.bank_api.dto.ClientDto;
import com.example.bank_api.entity.Client;
import com.example.bank_api.exception.ClientDuplicateException;
import com.example.bank_api.exception.ClientNotFoundException;
import com.example.bank_api.repository.ClientRepository;
import com.example.bank_api.service.ClientService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Log4j2
public class ClientServiceImpl implements ClientService {

    private final ClientRepository clientRepository;

    @Autowired
    public ClientServiceImpl(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    @Override
    public List<ClientDto> findAll() {
        List<ClientDto> list = clientRepository.findAll()
                .stream()
                .map(it -> ClientDto.valueOf(it))
                .collect(Collectors.toList());

        log.debug("Список всех клиентов: " + list);
        return list;
    }

    @Override
    public ClientDto findById(Long id) {
        ClientDto clientDto = clientRepository.findById(id)
                .map(it -> ClientDto.valueOf(it))
                .orElseThrow(() -> new ClientNotFoundException(id));

        log.debug("По id=" + id + " получен клиент: " + clientDto);
        return clientDto;
    }

    @Override
    public ClientDto save(ClientDto clientDto) {
        String last = clientDto.getLastname();
        String first = clientDto.getFirstname();
        String mid = clientDto.getMiddlename();

        if (clientRepository.findByLastnameAndFirstnameAndMiddlename(last, first, mid) == null) {
            Client client = clientDto.mapToClient();
            ClientDto saved = ClientDto.valueOf(clientRepository.save(client));
            log.debug("В БД сохранён клиент: " + saved);
            return saved;
        } else {
            String message = String.format("В БД уже есть клиент с полным именем '%s %s %s'", last, first, mid);
            throw new ClientDuplicateException(message);
        }
    }

    @Override
    @Transactional // иначе будет TransactionRequiredException: Executing an update/delete query
    public void update(Long id, ClientDto clientDto) {
        Client currentClient = clientRepository.findById(id).orElseThrow(() -> new ClientNotFoundException(id));

        Client client = clientDto.mapToClient();

        // Во время обновления изменять только ФИО и возраст
        String last = client.getLastname();
        String first = client.getFirstname();
        String mid = client.getMiddlename();
        Integer age = client.getAge();

        String data = String.format("'%s %s %s', возраст: %d", last, first, mid, age);

        log.debug("Обновить текущего клиента " + currentClient + " данными: " + data);

        clientRepository.updateNameAndAge(id, last, first, mid, age);
    }

    @Override
    public void delete(Long id) {
        Client client = clientRepository.findById(id).orElseThrow(() -> new ClientNotFoundException(id));
        log.debug("Удалить клиента: " + client);
        clientRepository.deleteById(id);
    }
}
