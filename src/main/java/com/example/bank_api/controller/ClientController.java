package com.example.bank_api.controller;

import com.example.bank_api.dto.ClientDto;
import com.example.bank_api.service.ClientService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/v1/client")
@Api(description = "Контроллер для клиентов")
public class ClientController {

    @Autowired
    private ClientService clientService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Получить всех клиентов")
    public List<ClientDto> findAll() {
        return clientService.findAll();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Получить клиента по id")
    public ClientDto findById(@PathVariable("id") Long id) {
        ClientDto clientDto = clientService.findById(id);
        if (clientDto == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return clientDto;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation(value = "Добавить клиента")
    public ClientDto save(@RequestBody ClientDto clientDto) {
        ClientDto saved = clientService.save(clientDto);
        if (saved == null) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY);
        }
        return saved;
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Обновить клиента")
    public void update(@PathVariable("id") Long id, @RequestBody ClientDto clientDto) {
        boolean updated = clientService.update(id, clientDto);
        if (updated == false) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ApiOperation(value = "Удалить клиента")
    public void delete(@PathVariable("id") Long id) {
        clientService.delete(id);
    }
}
