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
     * <p>Возвращает счёт по идентификаторам</p>
     *
     * @param clientId  Идентификатор клиента
     * @param accountId Идентификатор счёта
     * @return Счёт
     */
    AccountDto findById(Long clientId, Long accountId);

    /**
     * <p>Возвращает счёт по идентификатору клиента и номеру карты</p>
     *
     * @param clientId   Идентификатор клиента
     * @param cardNumber Номер карты
     * @return Счёт
     */
    AccountDto findAccountByCardNumber(Long clientId, String cardNumber);

    /**
     * <p>Обновляет счёт: устанавливает новое значение баланса</p>
     *
     * @param clientId   Идентификатор клиента
     * @param accountId  Идентификатор счёта
     * @param accountDto Данные счёта для обновления
     * @return <b>true</b> если счёт найден и обновлён, <b>false</b> если не обновлён
     */
    boolean updateAccountSetBalance(Long clientId, Long accountId, AccountDto accountDto);

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
