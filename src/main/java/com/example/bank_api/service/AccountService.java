package com.example.bank_api.service;

import com.example.bank_api.entity.Account;

import java.util.List;

public interface AccountService {

    List<Account> findAll();
}
