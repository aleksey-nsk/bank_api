//package com.example.bank_api.repository;
//
//import com.example.bank_api.entity.Account;
//import com.example.bank_api.entity.Client;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Modifying;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.stereotype.Repository;
//
//import java.math.BigDecimal;
//import java.util.List;
//
//@Repository
//public interface AccountRepository extends JpaRepository<Account, Long> {
//
//    List<Account> findAccountByClientId(Long clientId);
//
//    Account findAccountByIdAndClient_Id(Long accountId, Long clientId);
//
//    @Query("UPDATE Account SET balance = :balance WHERE id = :accountId")
//    @Modifying
//    void updateAccountSetBalance(Long accountId, BigDecimal balance);
//
//    @Query("UPDATE Account SET client = :client WHERE id = :accountId")
//    @Modifying
//    void updateAccountSetClient(Client client, Long accountId);
//}
