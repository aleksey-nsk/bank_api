package com.example.bank_api.service.impl;

import com.example.bank_api.dto.ClientDto;
import com.example.bank_api.entity.Account;
import com.example.bank_api.entity.Card;
import com.example.bank_api.entity.Client;
//import com.example.bank_api.repository.AccountRepository;
//import com.example.bank_api.repository.CardRepository;
import com.example.bank_api.exception.ClientNotFoundException;
import com.example.bank_api.repository.ClientRepository;
import com.example.bank_api.service.ClientService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Service
@Log4j2
public class ClientServiceImpl implements ClientService {

    private final ClientRepository clientRepository;

    @Autowired
    public ClientServiceImpl(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

//    @Autowired
//    private AccountRepository accountRepository;
//
//    @Autowired
//    private CardRepository cardRepository;

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
//        ClientDto clientDto = null;
//
//        Optional<Client> optionalClient = clientRepository.findById(id);
//        if (optionalClient.isPresent()) {
//            clientDto = ClientDto.valueOf(optionalClient.get());
//        }
//
//        log.debug("По id=" + id + " получен клиент: " + clientDto);
//        return clientDto;

        // !!!!!! вижу тип Client а не OptionalClient !!!!!!!!!!!!!!!!!!!!!!!!!!!
//        Client client = clientRepository.findById(id).orElseThrow(() -> new ClientNotFoundException(id));

        ClientDto clientDto = clientRepository.findById(id)
                .map(it -> ClientDto.valueOf(it))
                .orElseThrow(() -> new ClientNotFoundException(id));

        log.debug("По id=" + id + " получен клиент: " + clientDto);
        return clientDto;
    }

    @Override
    public ClientDto save(ClientDto clientDto) {
        return null;

//        String last = clientDto.getLastname();
//        String first = clientDto.getFirstname();
//        String mid = clientDto.getMiddlename();
//
//        Client client = clientDto.mapToClient();
//        ClientDto saved = null;
//
//        if (clientRepository.findByLastnameAndFirstnameAndMiddlename(last, first, mid) == null) {
//            saved = ClientDto.valueOf(clientRepository.save(client));
//            log.debug("В БД сохранён клиент: " + saved);
//        } else {
//            String fullName = String.format("%s %s %s", last, first, mid);
//            log.debug("В БД уже есть клиент с полным именем: '" + fullName + "'");
//        }
//
//        return saved;
    }

    @Override
    @Transactional
    public boolean update(Long id, ClientDto clientDto) {
        return false;

//        Client client = clientDto.mapToClient();
//        boolean updated = false;
//
//        Optional<Client> currentClient = clientRepository.findById(id);
//        if (currentClient.isPresent()) {
//            log.debug("Текущий клиент: " + currentClient.get());
//
//            // Во время обновления изменять только ФИО и возраст
//            String last = client.getLastname();
//            String first = client.getFirstname();
//            String mid = client.getMiddlename();
//            Integer age = client.getAge();
//            String data = String.format("'%s %s %s', возраст: %d", last, first, mid, age);
//            log.debug("Данные клиента для обновления: " + data);
//
//            clientRepository.updateNameAndAge(id, last, first, mid, age);
//            updated = true;
//        } else {
//            log.debug("В БД отсутствует клиент с id=" + id);
//        }
//
//        return updated;
    }

//    @Override
//    public void delete(Long clientId) {
//        log.debug("Удалить из БД клиента");
//
////        List<Account> accountList = accountRepository.findAccountByClientId(clientId);
////        for (Account account : accountList) {
////            List<Card> cardList = cardRepository.findCardByAccount_Id(account.getId());
////            for (Card card : cardList) {
////                log.debug("  удалить карты с идентификатором: " + card.getId());
////                cardRepository.deleteById(card.getId());
////            }
////            log.debug("  удалить счёт с идентификатором: " + account.getId());
////            accountRepository.deleteById(account.getId());
////        }
//
//        if (clientRepository.findById(clientId).isPresent()) {
//            log.debug("  удалить клиента с идентификатором: " + clientId);
//            clientRepository.deleteById(clientId);
//        } else {
//            log.debug("В БД отсутствует клиент с id=" + clientId);
//        }
//    }
}
