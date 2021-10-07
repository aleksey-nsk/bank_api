package com.example.bank_api.service;

import com.example.bank_api.dto.CardDto;

/**
 * @author Aleksey Zhdanov
 * @version 1
 */
public interface CardService {

    /**
     * <p>Добавляет новую карту по счёту</p>
     *
     * @param clientId  Идентификатор клиента
     * @param accountId Идентификатор счёта
     * @return Сохранённая в БД карта
     */
    CardDto save(Long clientId, Long accountId);

    /**
     * <p>Удаляет карту из БД</p>
     *
     * @param clientId Идентификатор клиента
     * @param cardId   Идентификатор карты
     */
    void delete(Long clientId, Long cardId);
}
