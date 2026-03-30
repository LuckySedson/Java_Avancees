package com.stock.stock_app.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "bon_sortie")
public class BonSortie {

    @Id
    @Column(name = "num_bon_sortie")
    private String numBonSortie;

    @Column(name = "num_produit")
    private String numProduit;

    @Column(name = "qte_sortie")
    private int qteSortie;

    @Column(name = "date_sortie")
    private String dateSortie;

    // Constructeurs
    public BonSortie() {
    }

    public BonSortie(String numBonSortie, String numProduit, int qteSortie, String dateSortie) {
        this.numBonSortie = numBonSortie;
        this.numProduit = numProduit;
        this.qteSortie = qteSortie;
        this.dateSortie = dateSortie;
    }

    // Getters & Setters
    public String getNumBonSortie() {
        return numBonSortie;
    }

    public void setNumBonSortie(String numBonSortie) {
        this.numBonSortie = numBonSortie;
    }

    public String getNumProduit() {
        return numProduit;
    }

    public void setNumProduit(String numProduit) {
        this.numProduit = numProduit;
    }

    public int getQteSortie() {
        return qteSortie;
    }

    public void setQteSortie(int qteSortie) {
        this.qteSortie = qteSortie;
    }

    public String getDateSortie() {
        return dateSortie;
    }

    public void setDateSortie(String dateSortie) {
        this.dateSortie = dateSortie;
    }
}