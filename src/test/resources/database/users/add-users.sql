INSERT INTO users (id, email, password, first_name, last_name, role)
VALUES (1,
        "bob@gmail.com",
        "$2a$10$MftFdz42cvwYagDZhqRVb.tqq/1iIFaYRxOvEOhltQ/AseB6RES3O", -- 12345678 not scripted password value
        "Bob",
        "Marley",
        "CUSTOMER");
INSERT INTO users (id, email, password, first_name, last_name, role)
VALUES (2,
        "admin@gmail.com",
        "$2a$10$MftFdz42cvwYagDZhqRVb.tqq/1iIFaYRxOvEOhltQ/AseB6RES3O", -- 12345678 not scripted password value
        "Admin",
        "Adminich",
        "MANAGER");