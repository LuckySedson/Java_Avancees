package com.stock.stock_app.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "bon_entree")
public class BonEntree {

    @Id
    @Column(name = "num_bon_entree")
    private String numBonEntree;

    @Column(name = "num_produit")
    private String numProduit;

    @Column(name = "qte_entree")
    private int qteEntree;

    @Column(name = "date_entree")
    private String dateEntree;

    // Constructeurs
    public BonEntree() {
    }

    public BonEntree(String numBonEntree, String numProduit, int qteEntree, String dateEntree) {
        this.numBonEntree = numBonEntree;
        this.numProduit = numProduit;
        this.qteEntree = qteEntree;
        this.dateEntree = dateEntree;
    }

    // Getters & Setters
    public String getNumBonEntree() {
        return numBonEntree;
    }

    public void setNumBonEntree(String numBonEntree) {
        this.numBonEntree = numBonEntree;
    }

    public String getNumProduit() {
        return numProduit;
    }

    public void setNumProduit(String numProduit) {
        this.numProduit = numProduit;
    }

    public int getQteEntree() {
        return qteEntree;
    }

    public void setQteEntree(int qteEntree) {
        this.qteEntree = qteEntree;
    }

    public String getDateEntree() {
        return dateEntree;
    }

    public void setDateEntree(String dateEntree) {
        this.dateEntree = dateEntree;
    }
}