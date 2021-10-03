package com.example.bank_api.service.impl;

import com.example.bank_api.dto.AccountDto;
import com.example.bank_api.dto.CardDto;
import com.example.bank_api.repository.CardRepository;
import com.example.bank_api.service.AccountService;
import com.example.bank_api.service.CardService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Log4j2
public class CardServiceImpl implements CardService {

    @Autowired
    private CardRepository cardRepository;

    @Autowired
    private AccountService accountService;

    @Override
    public List<CardDto> findAll(Long clientId, Long accountId) {
        log.debug("");
        log.debug("Поиск всех карточек по идентификаторам");
        log.debug("clientId: " + clientId);
        log.debug("accountId: " + accountId);

        List<CardDto> cardDtoList = null;

        AccountDto accountDto = accountService.findById(clientId, accountId);
        if (accountDto != null) {
            cardDtoList = cardRepository.findAllByAccount_Id(accountId)
                    .stream()
                    .map(it -> CardDto.valueOf(it))
                    .collect(Collectors.toList());
        }

        log.debug("cardDtoList: " + cardDtoList);
        return cardDtoList;
    }
}
