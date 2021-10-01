package com.example.bank_api.controller;

import com.example.bank_api.dto.AccountDto;
import com.example.bank_api.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/v1/client")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @GetMapping("/{client_id}/account")
    @ResponseStatus(HttpStatus.OK)
    public List<AccountDto> findAll(@PathVariable("client_id") Long clientId) {
        List<AccountDto> accountDtoList = accountService.findAll(clientId);
        if (accountDtoList == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return accountDtoList;
    }
}
