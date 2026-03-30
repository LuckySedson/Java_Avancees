package com.stock.stockclient.model;

public class BonSortie {
    private String numBonSortie;
    private String numProduit;
    private int qteSortie;
    private String dateSortie;

    public BonSortie() {}

    public String getNumBonSortie() { return numBonSortie; }
    public void setNumBonSortie(String v) { this.numBonSortie = v; }

    public String getNumProduit() { return numProduit; }
    public void setNumProduit(String v) { this.numProduit = v; }

    public int getQteSortie() { return qteSortie; }
    public void setQteSortie(int v) { this.qteSortie = v; }

    public String getDateSortie() { return dateSortie; }
    public void setDateSortie(String v) { this.dateSortie = v; }
}
