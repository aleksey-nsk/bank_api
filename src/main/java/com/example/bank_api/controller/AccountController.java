package com.example.bank_api.controller;

import com.example.bank_api.dto.AccountDto;
import com.example.bank_api.service.AccountService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/client/{client_id}/account")
@Api(description = "Контроллер для счетов")
public class AccountController {

    private final AccountService accountService;

    @Autowired
    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Получить все счета клиента")
    public List<AccountDto> findAll(@PathVariable("client_id") Long clientId) {
        return accountService.findAll(clientId);
    }

    @GetMapping("/{account_id}")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Получить счёт по id")
    public AccountDto findById(
            @PathVariable("client_id") Long clientId,
            @PathVariable("account_id") Long accountId
    ) {
        return accountService.findById(clientId, accountId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation(value = "Добавить счёт клиенту (без карт)")
    public AccountDto save(@PathVariable("client_id") Long clientId) {
        return accountService.save(clientId);
    }

    @PutMapping("/card")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Внести деньги на счёт")
    public void updateAccountAddBalanceByCardNumber(
            @PathVariable("client_id") Long clientId,
            @RequestParam("cardNumber") String cardNumber,
            @RequestParam("add") BigDecimal add
    ) {
        accountService.updateAccountAddBalanceByCardNumber(clientId, cardNumber, add);
    }
}
