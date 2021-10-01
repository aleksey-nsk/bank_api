package com.example.bank_api.service;

import com.example.bank_api.entity.Client;

import java.util.List;

public interface ClientService {

    List<Client> findAll();

    Client findById(Long id);

    Client save(Client client);

    Client update(Long id, Client newClient);

    void delete(Long id);
}
