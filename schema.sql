-- Créer la base (si pas encore fait)
CREATE DATABASE gestion_stock;

-- Table Produit
CREATE TABLE produit (
    num_produit VARCHAR(50) PRIMARY KEY,
    design VARCHAR(100) NOT NULL,
    stock INTEGER NOT NULL DEFAULT 0
);

-- Table BonEntree
CREATE TABLE bon_entree (
    num_bon_entree VARCHAR(50) PRIMARY KEY,
    num_produit VARCHAR(50) REFERENCES produit(num_produit),
    qte_entree INTEGER NOT NULL,
    date_entree VARCHAR(20)
);

-- Table BonSortie
CREATE TABLE bon_sortie (
    num_bon_sortie VARCHAR(50) PRIMARY KEY,
    num_produit VARCHAR(50) REFERENCES produit(num_produit),
    qte_sortie INTEGER NOT NULL,
    date_sortie VARCHAR(20)
);

-- Données de test
INSERT INTO produit VALUES ('P01', 'Blé', 100);
INSERT INTO produit VALUES ('P02', 'Riz', 200);

INSERT INTO bon_entree VALUES ('BE01', 'P01', 50, '10/12/09');
INSERT INTO bon_entree VALUES ('BE02', 'P01', 20, '21/12/09');

INSERT INTO bon_sortie VALUES ('BS01', 'P01', 5, '25/02/10');