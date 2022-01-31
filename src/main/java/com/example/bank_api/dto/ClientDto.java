package com.example.bank_api.dto;

import com.example.bank_api.entity.Account;
import com.example.bank_api.entity.Client;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientDto {

    private Long id;
    private String lastname;
    private String firstname;
    private String middlename;
    private Integer age;
    private List<Account> accounts;

    public ClientDto(String lastname, String firstname, String middlename, Integer age, List<Account> accounts) {
        this.lastname = lastname;
        this.firstname = firstname;
        this.middlename = middlename;
        this.age = age;
        this.accounts = accounts;
    }

    public static ClientDto valueOf(Client client) {
        return new ClientDto(
                client.getId(),
                client.getLastname(),
                client.getFirstname(),
                client.getMiddlename(),
                client.getAge(),
                client.getAccounts()
        );
    }

    public Client mapToClient() {
        return new Client(id, lastname, firstname, middlename, age, accounts);
    }
}
