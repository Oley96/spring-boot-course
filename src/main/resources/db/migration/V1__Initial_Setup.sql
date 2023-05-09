CREATE TABLE customer(
    id    BIGSERIAL PRIMARY KEY,
    name  TEXT NOT NULL,
    email TEXT UNIQUE NOT NULL,
    age   INT NOT NULL
);