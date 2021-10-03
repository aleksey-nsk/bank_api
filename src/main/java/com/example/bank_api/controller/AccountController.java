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

    @PutMapping("/{client_id}/account/{account_id}")
    @ResponseStatus(HttpStatus.OK)
    public void update(
            @PathVariable("client_id") Long clientId,
            @PathVariable("account_id") Long accountId,
            @RequestBody AccountDto accountDto
    ) {
        boolean updated = accountService.update(clientId, accountId, accountDto);
        if (updated == false) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }
}
