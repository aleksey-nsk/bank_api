package com.example.bank_api.controller;

import com.example.bank_api.dto.ClientDto;
import com.example.bank_api.service.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/v1/client")
public class ClientController {

    @Autowired
    private ClientService clientService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ClientDto> findAll() {
        return clientService.findAll();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ClientDto findById(@PathVariable("id") Long id) {
        ClientDto clientDto = clientService.findById(id);
        if (clientDto == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return clientDto;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ClientDto save(@RequestBody ClientDto clientDto) {
        ClientDto saved = clientService.save(clientDto);
        if (saved == null) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY);
        }
        return saved;
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void update(@PathVariable("id") Long id, @RequestBody ClientDto clientDto) {
        boolean updated = clientService.update(id, clientDto);
        if (updated == false) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") Long id) {
        clientService.delete(id);
    }
}
