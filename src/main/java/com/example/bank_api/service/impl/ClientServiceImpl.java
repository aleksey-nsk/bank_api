package com.example.bank_api.service.impl;

import com.example.bank_api.entity.Client;
import com.example.bank_api.repository.ClientRepository;
import com.example.bank_api.service.ClientService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Log4j2
public class ClientServiceImpl implements ClientService {

    @Autowired
    private ClientRepository clientRepository;


    @Override
    public List<Client> findAll() {
        List<Client> clientList = clientRepository.findAll();
        log.debug("Список всех клиентов: " + clientList);
        return clientList;
    }

    @Override
    public Client findById(Long id) {
        Client client = null;

        Optional<Client> optionalClient = clientRepository.findById(id);
        if (optionalClient.isPresent()) {
            client = optionalClient.get();
        }

        log.debug("По id=" + id + " получен клиент: " + client);
        return client;
    }

    @Override
    public Client save(Client client) {
        String last = client.getLastname();
        String first = client.getFirstname();
        String mid = client.getMiddlename();

        Client saved = null;

        if (clientRepository.findByLastnameAndFirstnameAndMiddlename(last, first, mid) == null) {
            saved = clientRepository.save(client);
            log.debug("В БД сохранён клиент: " + saved);
        } else {
            String fullName = String.format("%s %s %s", last, first, mid);
            log.debug("В БД уже есть клиент с полным именем: '" + fullName + "'");
        }

        return saved;
    }

    @Override
    public Client update(Long id, Client newClient) {
        Client updatedClient = null;

        Optional<Client> oldClient = clientRepository.findById(id);
        if (oldClient.isPresent()) {
            log.debug("Текущий клиент: " + oldClient.get());

            // Во время обновления изменять ФИО и возраст.
            // При этом id и счета брать текущие
            newClient.setId(oldClient.get().getId());
            newClient.setAccounts(oldClient.get().getAccounts());
            log.debug("Новый клиент: " + newClient);

            updatedClient = clientRepository.save(newClient);
            log.debug("Обновлённый клиент: " + updatedClient);
        } else {
            log.debug("В БД отсутствует клиент с id=" + id);
        }

        return updatedClient;
    }

    @Override
    public void delete(Long id) {
        log.debug("Удалить из БД клиента с идентификатором: " + id);

        if (clientRepository.findById(id).isPresent()) {
            clientRepository.deleteById(id);
        } else {
            log.debug("В БД отсутствует клиент с id=" + id);
        }
    }
}
