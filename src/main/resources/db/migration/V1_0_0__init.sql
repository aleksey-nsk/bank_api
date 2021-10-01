CREATE TABLE clients
(
    id         bigserial PRIMARY KEY,
    lastname   varchar,
    firstname  varchar,
    middlename varchar,
    age        int
);

CREATE TABLE accounts
(
    id           bigserial PRIMARY KEY,
    number       varchar,
    opening_date date,
    client_id    bigint,
    FOREIGN KEY (client_id) REFERENCES clients (id)
);

CREATE TABLE cards
(
    id           bigserial PRIMARY KEY,
    number       varchar,
    release_date date,
    account_id   bigint,
    FOREIGN KEY (account_id) REFERENCES accounts (id)
);

INSERT INTO clients(lastname, firstname, middlename, age)
VALUES ('Иванов', 'Иван', 'Иванович', 34);

INSERT INTO accounts(number, opening_date, client_id)
VALUES ('11111222223333344444', '2017-01-14', (SELECT id FROM clients WHERE lastname = 'Иванов')),
       ('55555666667777788888', '2018-02-15', (SELECT id FROM clients WHERE lastname = 'Иванов'));

INSERT INTO cards(number, release_date, account_id)
VALUES ('0000001111122222', '2019-09-15', (SELECT id FROM accounts WHERE accounts.number = '11111222223333344444')),
       ('0000003333344444', '2019-10-16', (SELECT id FROM accounts WHERE accounts.number = '11111222223333344444')),
       ('0000005555566666', '2020-11-17', (SELECT id FROM accounts WHERE accounts.number = '55555666667777788888')),
       ('0000003333344444', '2020-12-18', (SELECT id FROM accounts WHERE accounts.number = '55555666667777788888'));
