package com.stock.stock_app.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "produit")
public class Produit {

    @Id
    @Column(name = "num_produit")
    private String numProduit;

    @Column(name = "design")
    private String design;

    @Column(name = "stock")
    private int stock;

    // Constructors
    public Produit() {
    }

    public Produit(String numProduit, String design, int stock) {
        this.numProduit = numProduit;
        this.design = design;
        this.stock = stock;
    }

    // Getters & Setters
    public String getNumProduit() {
        return numProduit;
    }

    public void setNumProduit(String numProduit) {
        this.numProduit = numProduit;
    }

    public String getDesign() {
        return design;
    }

    public void setDesign(String design) {
        this.design = design;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }
}