package com.example.bank_api.service.impl;

import com.example.bank_api.dto.AccountDto;
import com.example.bank_api.dto.ClientDto;
import com.example.bank_api.entity.Account;
import com.example.bank_api.repository.AccountRepository;
import com.example.bank_api.repository.ClientRepository;
import com.example.bank_api.service.AccountService;
import com.example.bank_api.service.ClientService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Log4j2
public class AccountServiceImpl implements AccountService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private ClientService clientService;

    @Override
    public List<AccountDto> findAll(Long clientId) {
        ClientDto clientDto = clientService.findById(clientId);

        if (clientDto != null) {

        }


        List<Account> accountList = accountRepository.findAll();
        log.debug("Список всех счетов: " + accountList);
        return accountList;
    }
}
