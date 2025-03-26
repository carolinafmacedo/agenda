CREATE DATABASE agenda;

CREATE TABLE Usuario (
    id_usuario SERIAL PRIMARY KEY,
    nome VARCHAR(50) NOT NULL,
    username VARCHAR(45) UNIQUE NOT NULL,
    senha VARCHAR(64) NOT NULL,
    telefone VARCHAR(15),
    rua VARCHAR(100),
    cidade VARCHAR(50),
    estado CHAR(50)
);

CREATE TABLE Contato (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    rua VARCHAR(100),
    cidade VARCHAR(50),
    estado CHAR(50),
    id_usuario INT NOT NULL,
    FOREIGN KEY (id_usuario) REFERENCES Usuario(id_usuario) ON DELETE CASCADE
);

CREATE TABLE Telefone (
    id SERIAL PRIMARY KEY,
    numero VARCHAR(15) NOT NULL,
    id_contato INT NOT NULL,
    FOREIGN KEY (id_contato) REFERENCES Contato(id) ON DELETE CASCADE
);