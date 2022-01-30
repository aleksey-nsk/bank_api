package com.example.bank_api.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.List;

import static javax.persistence.CascadeType.*;

@Entity
@Table(name = "clients")
@Data
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "lastname")
    private String lastname;

    @Column(name = "firstname")
    private String firstname;

    @Column(name = "middlename")
    private String middlename;

    @Column(name = "age")
    private Integer age;

    @OneToMany(mappedBy = "client", cascade = {DETACH, MERGE, PERSIST, REFRESH})
    private List<Account> accounts;

    public Client() {
    }

    public Client(Long id, String lastname, String firstname, String middlename, Integer age, List<Account> accounts) {
        this.id = id;
        this.lastname = lastname;
        this.firstname = firstname;
        this.middlename = middlename;
        this.age = age;
        this.accounts = accounts;
    }
}
