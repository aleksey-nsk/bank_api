package com.example.bank_api.repository;

import com.example.bank_api.entity.Account;
import com.example.bank_api.entity.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CardRepository extends JpaRepository<Card, Long> {

    List<Card> findAllByAccount_Id(Long accountId);

    Card findCardByNumber(String number);

    @Query("UPDATE Card SET account = :account WHERE id = :cardId")
    @Modifying
    void updateCardSetAccount(Account account, Long cardId);
}
