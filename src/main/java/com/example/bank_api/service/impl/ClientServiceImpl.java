package com.example.bank_api.service.impl;

import com.example.bank_api.dto.ClientDto;
import com.example.bank_api.entity.Client;
import com.example.bank_api.exception.ClientDuplicateException;
import com.example.bank_api.exception.ClientNotFoundException;
import com.example.bank_api.repository.AccountRepository;
import com.example.bank_api.repository.CardRepository;
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
    private final AccountRepository accountRepository;
    private final CardRepository cardRepository;

    @Autowired
    public ClientServiceImpl(ClientRepository clientRepository, AccountRepository accountRepository, CardRepository cardRepository) {
        this.clientRepository = clientRepository;
        this.accountRepository = accountRepository;
        this.cardRepository = cardRepository;
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

        if (clientRepository.findByLastnameAndFirstnameAndMiddlename(last, first, mid) != null) {
            String message = String.format("В БД уже есть клиент с полным именем '%s %s %s'", last, first, mid);
            throw new ClientDuplicateException(message);
        }

        Client client = clientDto.mapToClient();
        ClientDto saved = ClientDto.valueOf(clientRepository.save(client));
        log.debug("В БД сохранён клиент: " + saved);
        return saved;
    }

    @Override
    @Transactional // иначе будет ошибка: "TransactionRequiredException: Executing an update/delete query"
    public void update(Long id, ClientDto clientDto) {
        Client currentClient = clientRepository.findById(id).orElseThrow(() -> new ClientNotFoundException(id));

        // Во время обновления изменять только ФИО и возраст
        Client client = clientDto.mapToClient();
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

        client.getAccounts()
                .stream()
                .flatMap(account -> account.getCards().stream())
                .collect(Collectors.toList())
                .forEach(card -> {
                    log.debug("  удалить карту с cardId=" + card.getId());
                    cardRepository.deleteById(card.getId());
                });

        client.getAccounts()
                .forEach(account -> {
                    log.debug("  удалить счёт с accountId=" + account.getId());
                    accountRepository.deleteById(account.getId());
                });

        log.debug("  удалить самого клиента");
        clientRepository.deleteById(id);
    }
}
