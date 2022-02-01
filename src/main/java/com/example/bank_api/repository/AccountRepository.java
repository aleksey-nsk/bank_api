package com.example.bank_api.repository;

import com.example.bank_api.entity.Account;
import com.example.bank_api.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    @Query("UPDATE Account SET client = :client WHERE id = :accountId")
    @Modifying
    void updateAccountSetClient(Client client, Long accountId);

    @Query("UPDATE Account SET balance = :balance WHERE id = :accountId")
    @Modifying
    void updateAccountSetBalance(Long accountId, BigDecimal balance);
}
