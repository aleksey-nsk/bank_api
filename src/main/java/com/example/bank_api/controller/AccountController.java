package com.example.bank_api.controller;

import com.example.bank_api.dto.AccountDto;
import com.example.bank_api.service.AccountService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/client/{client_id}/account")
@Api(description = "Контроллер для счетов")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Получить все счета клиента")
    public List<AccountDto> findAll(@PathVariable("client_id") Long clientId) {
        List<AccountDto> accountDtoList = accountService.findAll(clientId);
        if (accountDtoList == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return accountDtoList;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation(value = "Добавить счёт клиенту (без карт)")
    public AccountDto save(@PathVariable("client_id") Long clientId) {
        AccountDto saved = accountService.save(clientId);
        if (saved == null) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY);
        }
        return saved;
    }

    @PutMapping("/card")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Внести деньги на счёт")
    public void updateAccountAddBalanceByCardNumber(
            @PathVariable("client_id") Long clientId,
            @RequestParam("number") String cardNumber,
            @RequestParam("add") BigDecimal add
    ) {
        boolean updated = accountService.updateAccountAddBalanceByCardNumber(clientId, cardNumber, add);
        if (updated == false) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }
}
