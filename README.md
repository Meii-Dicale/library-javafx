-- ====================================================================================
-- Script SQL (Version compatible multi-Bases de Données)
-- Création de la base de données pour la gestion de bibliothèque
-- ====================================================================================

-- Les commandes pour créer et utiliser une base de données peuvent varier.
-- Vous devrez peut-être exécuter ces commandes séparément dans DBeaver.
-- CREATE DATABASE library_db;
-- USE library_db; -- (ou vous connecter directement à la base via l'interface)


-- ====================================================================================
-- 1. Création des tables indépendantes
-- ====================================================================================

CREATE TABLE Author (
    id SERIAL PRIMARY KEY, -- SERIAL est un raccourci pour INT AUTO_INCREMENT dans PostgreSQL
    firstname VARCHAR(50) NOT NULL,
    lastname VARCHAR(50)
);

CREATE TABLE Users ( -- Renommé de "User" à "Users"
    id SERIAL PRIMARY KEY,
    user_name VARCHAR(50) NOT NULL,
    password VARCHAR(255) NOT NULL,
    is_admin BOOLEAN DEFAULT FALSE,
    mail VARCHAR(100) NOT NULL UNIQUE,
    phone_number VARCHAR(20)
);

CREATE TABLE PhysicalState ( -- Renommé de "State" à "PhysicalState"
    id SERIAL PRIMARY KEY,
    state_name VARCHAR(50) NOT NULL
);

CREATE TABLE Category ( -- Renommé de "Type" à "Category"
    id SERIAL PRIMARY KEY,
    type_name VARCHAR(50) NOT NULL
);


-- ====================================================================================
-- 2. Création des tables dépendantes
-- ====================================================================================

CREATE TABLE Media (
    id SERIAL PRIMARY KEY,
    title VARCHAR(100) NOT NULL,
    edition VARCHAR(100),
    year SMALLINT, -- SMALLINT est plus standard que YEAR
    summary TEXT,
    author_id INT,
    CONSTRAINT fk_author FOREIGN KEY (author_id) REFERENCES Author(id) ON DELETE SET NULL
);

CREATE TABLE Stock (
    id SERIAL PRIMARY KEY,
    is_available BOOLEAN DEFAULT TRUE,
    media_id INT NOT NULL,
    physic_state_id INT,
    CONSTRAINT fk_media FOREIGN KEY (media_id) REFERENCES Media(id) ON DELETE CASCADE,
    CONSTRAINT fk_physic_state FOREIGN KEY (physic_state_id) REFERENCES PhysicalState(id)
);

CREATE TABLE Reservation (
    id SERIAL PRIMARY KEY,
    started_at_date DATE NOT NULL,
    ended_at_date DATE,
    is_ended BOOLEAN DEFAULT FALSE,
    user_id INT NOT NULL,
    stock_id INT NOT NULL,
    CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES Users(id),
    CONSTRAINT fk_stock FOREIGN KEY (stock_id) REFERENCES Stock(id)
);


-- ====================================================================================
-- 3. Création de la table de liaison (Many-to-Many)
-- ====================================================================================

CREATE TABLE Media_Category ( -- Table renommée en conséquence
    media_id INT NOT NULL,
    category_id INT NOT NULL,
    CONSTRAINT fk_media_liaison FOREIGN KEY (media_id) REFERENCES Media(id) ON DELETE CASCADE,
    CONSTRAINT fk_category_liaison FOREIGN KEY (category_id) REFERENCES Category(id) ON DELETE CASCADE,
    PRIMARY KEY (media_id, category_id)
);
