package com.example.bank_api.service;

import com.example.bank_api.dto.ClientDto;

import java.util.List;

/**
 * @author Aleksey Zhdanov
 * @version 1
 */
public interface ClientService {

    /**
     * <p>Возвращает список клиентов</p>
     *
     * @return Список клиентов
     */
    List<ClientDto> findAll();

    /**
     * <p>Возвращает клиента по идентификатору</p>
     *
     * @param id Идентификатор
     * @return Клиент
     */
    ClientDto findById(Long id);

    /**
     * <p>Добавляет нового клиента</p>
     *
     * @param clientDto Данные клиента для добавления
     * @return Сохранённый в БД клиент
     */
    ClientDto save(ClientDto clientDto);

    /**
     * <p>Обновляет клиента</p>
     *
     * @param id        Идентификатор клиента
     * @param clientDto Данные клиента для обновления
     */
    void update(Long id, ClientDto clientDto);

    /**
     * <p>Удаляет клиента из БД</p>
     *
     * @param id Идентификатор клиента
     */
    void delete(Long id);
}
