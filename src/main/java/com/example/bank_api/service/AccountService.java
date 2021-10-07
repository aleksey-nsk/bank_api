package com.example.bank_api.service;

import com.example.bank_api.dto.AccountDto;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author Aleksey Zhdanov
 * @version 1
 */
public interface AccountService {

    /**
     * <p>Возвращает список счетов клиента</p>
     *
     * @param clientId Идентификатор клиента
     * @return Список счетов
     */
    List<AccountDto> findAll(Long clientId);

    /**
     * <p>Добавляет счёт клиенту (без карт)</p>
     *
     * @param clientId
     * @return
     */
    AccountDto save(Long clientId);

    /**
     * <p>Обновляет счёт: добавляет деньги к текущему балансу</p>
     *
     * @param clientId   Идентификатор клиента
     * @param cardNumber Номер карты
     * @param add        Сумма денег для внесения на счёт
     * @return <b>true</b> если счёт найден и обновлён, <b>false</b> если не обновлён
     */
    boolean updateAccountAddBalanceByCardNumber(Long clientId, String cardNumber, BigDecimal add);
}
