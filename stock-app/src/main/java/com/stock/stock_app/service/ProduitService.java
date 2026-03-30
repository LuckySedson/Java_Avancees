package com.stock.stock_app.service;

import com.stock.stock_app.model.*;
import com.stock.stock_app.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProduitService {

    @Autowired
    private ProduitRepository produitRepo;

    @Autowired
    private BonEntreeRepository bonEntreeRepo;

    @Autowired
    private BonSortieRepository bonSortieRepo;

    // ---- PRODUIT ----
    public List<Produit> getAllProduits() {
        return produitRepo.findAll();
    }

    public Optional<Produit> getProduitById(String id) {
        return produitRepo.findById(id);
    }

    public List<Produit> searchByDesign(String design) {
        return produitRepo.findByDesignContainingIgnoreCase(design);
    }

    public Produit saveProduit(Produit p) {
        return produitRepo.save(p);
    }

    public void deleteProduit(String id) {
        produitRepo.deleteById(id);
    }

    // ---- BON ENTREE ----
    public List<BonEntree> getAllBonEntrees() {
        return bonEntreeRepo.findAll();
    }

    public BonEntree saveBonEntree(BonEntree be) {
        // Mettre à jour le stock
        produitRepo.findById(be.getNumProduit()).ifPresent(p -> {
            p.setStock(p.getStock() + be.getQteEntree());
            produitRepo.save(p);
        });
        return bonEntreeRepo.save(be);
    }

    public void deleteBonEntree(String id) {
        bonEntreeRepo.deleteById(id);
    }

    // ---- BON SORTIE ----
    public List<BonSortie> getAllBonSorties() {
        return bonSortieRepo.findAll();
    }

    public BonSortie saveBonSortie(BonSortie bs) {
        // Mettre à jour le stock
        produitRepo.findById(bs.getNumProduit()).ifPresent(p -> {
            p.setStock(p.getStock() - bs.getQteSortie());
            produitRepo.save(p);
        });
        return bonSortieRepo.save(bs);
    }

    public void deleteBonSortie(String id) {
        bonSortieRepo.deleteById(id);
    }

    // ---- MOUVEMENTS PRODUIT ----
    public List<BonEntree> getEntreesByProduit(String numProduit) {
        return bonEntreeRepo.findByNumProduit(numProduit);
    }

    public List<BonSortie> getSortiesByProduit(String numProduit) {
        return bonSortieRepo.findByNumProduit(numProduit);
    }
}
