package com.example.bank_api.service.impl;

import com.example.bank_api.dto.AccountDto;
import com.example.bank_api.dto.ClientDto;
import com.example.bank_api.repository.AccountRepository;
import com.example.bank_api.service.AccountService;
import com.example.bank_api.service.ClientService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Log4j2
public class AccountServiceImpl implements AccountService {

    @Autowired
    private AccountRepository accountRepository;

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
}
