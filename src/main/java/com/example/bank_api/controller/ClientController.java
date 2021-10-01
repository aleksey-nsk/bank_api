package com.example.bank_api.controller;

import com.example.bank_api.entity.Client;
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
    public List<Client> findAll() {
        return clientService.findAll();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Client findById(@PathVariable("id") Long id) {
        Client client = clientService.findById(id);
        if (client == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return client;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Client save(@RequestBody Client client) {
        Client saved = clientService.save(client);
        if (saved == null) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY);
        }
        return client;
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Client update(@PathVariable("id") Long id, @RequestBody Client newClient) {
        Client updatedClient = clientService.update(id, newClient);
        if (updatedClient == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return updatedClient;
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") Long id) {
        clientService.delete(id);
    }
}
