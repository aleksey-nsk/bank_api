package com.example.bank_api.service;

import com.example.bank_api.dto.CardDto;

import java.util.List;

public interface CardService {

    List<CardDto> findAll(Long clientId, Long accountId);
}
