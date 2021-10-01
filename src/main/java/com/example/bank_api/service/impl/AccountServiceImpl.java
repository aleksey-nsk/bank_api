package com.example.bank_api.service.impl;

import com.example.bank_api.entity.Account;
import com.example.bank_api.repository.AccountRepository;
import com.example.bank_api.service.AccountService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Log4j2
public class AccountServiceImpl implements AccountService {

    @Autowired
    private AccountRepository accountRepository;

    @Override
    public List<Account> findAll() {
        List<Account> accountList = accountRepository.findAll();
        log.debug("Список всех счетов: " + accountList);
        return accountList;
    }
}
