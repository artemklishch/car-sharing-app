INSERT INTO payments (id, status, type, rental_id, session, session_id, amount)
VALUES (1,
        "PAID",
        "PAYMENT",
        "1",
        "https://some-session-url-1.com",
        "some-session-id-1",
        "1050");
INSERT INTO payments (id, status, type, rental_id, session, session_id, amount)
VALUES (2,
        "PAID",
        "PAYMENT",
        "2",
        "https://some-session-url-2.com",
        "some-session-id-2",
        "1200");
INSERT INTO payments (id, status, type, rental_id, session, session_id, amount)
VALUES (3,
        "PAID",
        "FINE",
        "3",
        "https://some-session-url-3.com",
        "some-session-id-3",
        "2500");
INSERT INTO payments (id, status, type, rental_id, session, session_id, amount)
VALUES (4,
        "PENDING",
        "PAYMENT",
        "3",
        "https://some-session-url-4.com",
        "some-session-id-4",
        "2500");
