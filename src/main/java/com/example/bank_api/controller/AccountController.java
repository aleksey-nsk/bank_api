package com.example.bank_api.controller;

import com.example.bank_api.dto.AccountDto;
import com.example.bank_api.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/v1/client/{client_id}/account")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<AccountDto> findAll(@PathVariable("client_id") Long clientId) {
        List<AccountDto> accountDtoList = accountService.findAll(clientId);
        if (accountDtoList == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return accountDtoList;
    }

    @GetMapping("/card")
    @ResponseStatus(HttpStatus.OK)
    public AccountDto findAccountByCardNumber(
            @PathVariable("client_id") Long clientId,
            @RequestParam("number") String cardNumber
    ) {
        AccountDto accountDto = accountService.findAccountByCardNumber(clientId, cardNumber);
        if (accountDto == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return accountDto;
    }

    @PutMapping("/{account_id}")
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
