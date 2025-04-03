CREATE SCHEMA IF NOT EXISTS netology;

CREATE TABLE IF NOT EXISTS netology.users (
    login TEXT PRIMARY KEY,
    password TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS netology.auth_tokens (
    id SERIAL PRIMARY KEY,
    user_name TEXT REFERENCES netology.users(login),
    token TEXT UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS netology.files (
    id SERIAL PRIMARY KEY,
    user_name TEXT REFERENCES netology.users(login),
    filename TEXT NOT NULL,
    file_data BYTEA NOT NULL,
    hash TEXT NOT NULL,
    size BIGINT NOT NULL
);