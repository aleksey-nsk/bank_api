package com.example.bank_api.service;

import com.example.bank_api.dto.ClientDto;

import java.util.List;

public interface ClientService {

    List<ClientDto> findAll();

    ClientDto findById(Long id);

    ClientDto save(ClientDto clientDto);

    ClientDto update(Long id, ClientDto clientDto);

    void delete(Long id);
}
