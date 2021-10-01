package com.example.bank_api.service;

import com.example.bank_api.dto.AccountDto;

import java.util.List;

public interface AccountService {

    List<AccountDto> findAll(Long clientId);
}
