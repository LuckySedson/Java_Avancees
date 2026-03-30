package com.stock.stockclient.model;

public class Produit {
    private String numProduit;
    private String design;
    private int stock;

    public Produit() {}

    public String getNumProduit() { return numProduit; }
    public void setNumProduit(String numProduit) { this.numProduit = numProduit; }

    public String getDesign() { return design; }
    public void setDesign(String design) { this.design = design; }

    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }

    @Override
    public String toString() { return design; }
}
