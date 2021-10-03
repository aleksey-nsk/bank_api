package com.example.bank_api.service.impl;

import com.example.bank_api.dto.AccountDto;
import com.example.bank_api.dto.ClientDto;
import com.example.bank_api.entity.Account;
import com.example.bank_api.entity.Card;
import com.example.bank_api.entity.Client;
import com.example.bank_api.repository.AccountRepository;
import com.example.bank_api.repository.CardRepository;
import com.example.bank_api.service.AccountService;
import com.example.bank_api.service.ClientService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Log4j2
public class AccountServiceImpl implements AccountService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private CardRepository cardRepository;

    @Autowired
    private ClientService clientService;

    @Override
    public List<AccountDto> findAll(Long clientId) {
        List<AccountDto> accountDtoList = null;

        ClientDto clientDto = clientService.findById(clientId);
        if (clientDto != null) {
            accountDtoList = accountRepository.findAccountByClientId(clientId)
                    .stream()
                    .map(it -> AccountDto.valueOf(it))
                    .collect(Collectors.toList());
        }

        log.debug("Список всех счетов клиента: " + accountDtoList);
        return accountDtoList;
    }

    @Override
    public AccountDto findById(Long clientId, Long accountId) {
        log.debug("");
        log.debug("Поиск счёта по идентификаторам");
        log.debug("clientId: " + clientId);
        log.debug("accountId: " + accountId);

        AccountDto accountDto = null;

        Account account = accountRepository.findAccountByIdAndClient_Id(accountId, clientId);
        if (account != null) {
            accountDto = AccountDto.valueOf(account);
        }

        log.debug("accountDto: " + accountDto);
        return accountDto;
    }

    @Override
    public AccountDto findAccountByCardNumber(Long clientId, String cardNumber) {
        log.debug("");
        log.debug("Поиск счёта по номеру карты");
        log.debug("clientId: " + clientId);
        log.debug("cardNumber: " + cardNumber);

        AccountDto accountDto = null;

        ClientDto clientDto = clientService.findById(clientId);
        Card card = cardRepository.findCardByNumber(cardNumber);
        log.debug("card: " + card);

        if (clientDto != null && card != null) {
            Account accountByCard = card.getAccount();
            log.debug("accountByCard: " + accountByCard);

            Client clientByAccount = accountByCard.getClient();
            log.debug("clientByAccount: " + clientByAccount);

            if (clientByAccount.getId().equals(clientId)) {
                accountDto = AccountDto.valueOf(accountByCard);
            }
        }

        return accountDto;
    }

    @Override
    @Transactional
    public boolean update(Long clientId, Long accountId, AccountDto accountDto) {
        Account account = accountDto.mapToAccount();
        boolean updated = false;

        Account currentAccount = accountRepository.findAccountByIdAndClient_Id(accountId, clientId);
        if (currentAccount != null) {
            log.debug("Текущий счёт: " + currentAccount);

            // Во время обновления изменять только баланс на счету
            BigDecimal balance = account.getBalance();
            log.debug("Обновить баланс на счёте: " + balance);

            accountRepository.updateAccountSetBalance(accountId, balance);
            updated = true;
        } else {
            log.debug("В БД отсутствует счёт с clientId=" + clientId + " и accountId=" + accountId);
        }

        return updated;
    }
}
