package com.example.bank_api.repository;

import com.example.bank_api.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {

    Client findByLastnameAndFirstnameAndMiddlename(String lastname, String firstname, String middlename);
}
