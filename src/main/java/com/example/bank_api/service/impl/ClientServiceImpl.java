package com.example.bank_api.service.impl;

import com.example.bank_api.dto.ClientDto;
import com.example.bank_api.entity.Client;
import com.example.bank_api.repository.ClientRepository;
import com.example.bank_api.service.ClientService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Log4j2
public class ClientServiceImpl implements ClientService {

    @Autowired
    private ClientRepository clientRepository;


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
        ClientDto clientDto = null;

        Optional<Client> optionalClient = clientRepository.findById(id);
        if (optionalClient.isPresent()) {
            clientDto = ClientDto.valueOf(optionalClient.get());
        }

        log.debug("По id=" + id + " получен клиент: " + clientDto);
        return clientDto;
    }

    @Override
    public ClientDto save(ClientDto clientDto) {
        String last = clientDto.getLastname();
        String first = clientDto.getFirstname();
        String mid = clientDto.getMiddlename();

        Client client = clientDto.mapToClient();
        ClientDto saved = null;

        if (clientRepository.findByLastnameAndFirstnameAndMiddlename(last, first, mid) == null) {
            saved = ClientDto.valueOf(clientRepository.save(client));
            log.debug("В БД сохранён клиент: " + saved);
        } else {
            String fullName = String.format("%s %s %s", last, first, mid);
            log.debug("В БД уже есть клиент с полным именем: '" + fullName + "'");
        }

        return saved;
    }

    @Override
    public ClientDto update(Long id, ClientDto clientDto) {
        Client client = clientDto.mapToClient();
        ClientDto updated = null;

        Optional<Client> currentClient = clientRepository.findById(id);
        if (currentClient.isPresent()) {
            log.debug("Текущий клиент: " + currentClient.get());

            // Во время обновления изменять ФИО и возраст.
            // При этом id и счета брать текущие
            client.setId(currentClient.get().getId());
            client.setAccounts(currentClient.get().getAccounts());
            log.debug("Данные клиента для обновления: " + client);

            updated = ClientDto.valueOf(clientRepository.save(client));
            log.debug("Обновлённый клиент: " + updated);
        } else {
            log.debug("В БД отсутствует клиент с id=" + id);
        }

        return updated;
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
