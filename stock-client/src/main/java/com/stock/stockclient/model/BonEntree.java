package com.stock.stockclient.model;

public class BonEntree {
    private String numBonEntree;
    private String numProduit;
    private int qteEntree;
    private String dateEntree;

    public BonEntree() {}

    public String getNumBonEntree() { return numBonEntree; }
    public void setNumBonEntree(String v) { this.numBonEntree = v; }

    public String getNumProduit() { return numProduit; }
    public void setNumProduit(String v) { this.numProduit = v; }

    public int getQteEntree() { return qteEntree; }
    public void setQteEntree(int v) { this.qteEntree = v; }

    public String getDateEntree() { return dateEntree; }
    public void setDateEntree(String v) { this.dateEntree = v; }
}
