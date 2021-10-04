package com.example.bank_api.service;

import com.example.bank_api.dto.CardDto;

import java.util.List;

/**
 * @author Aleksey Zhdanov
 * @version 1
 */
public interface CardService {

    /**
     * <p>Возвращает список карт клиента</p>
     *
     * @param clientId  Идентификатор клиента
     * @param accountId Идентификатор счёта
     * @return Список карт
     */
    List<CardDto> findAll(Long clientId, Long accountId);

    /**
     * <p>Добавляет новую карту по счёту</p>
     *
     * @param clientId  Идентификатор клиента
     * @param accountId Идентификатор счёта
     * @param cardDto   Данные карты для добавления
     * @return Сохранённая в БД карта
     */
    CardDto save(Long clientId, Long accountId, CardDto cardDto);
}
