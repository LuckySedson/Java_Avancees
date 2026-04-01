package com.stock.stock_app.controller;

import com.stock.stock_app.model.*;
import com.stock.stock_app.service.ProduitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class ProduitController {

    @Autowired
    private ProduitService service;

    // ---- PRODUITS ----
    @GetMapping("/produits")
    public List<Produit> getAllProduits() {
        return service.getAllProduits();
    }

    @GetMapping("/produits/{id}")
    public Optional<Produit> getProduit(@PathVariable String id) {
        return service.getProduitById(id);
    }

    @GetMapping("/produits/search")
    public List<Produit> searchProduit(@RequestParam String design) {
        return service.searchByDesign(design);
    }

    @PostMapping("/produits")
    public Produit addProduit(@RequestBody Produit p) {
        return service.saveProduit(p);
    }

    @PutMapping("/produits/{id}")
    public Produit updateProduit(@PathVariable String id, @RequestBody Produit p) {
        p.setNumProduit(id);
        return service.saveProduit(p);
    }

    @DeleteMapping("/produits/{id}")
    public void deleteProduit(@PathVariable String id) {
        service.deleteProduit(id);
    }

    // ---- BON ENTREE ----
    @GetMapping("/bonentrees")
    public List<BonEntree> getAllBonEntrees() {
        return service.getAllBonEntrees();
    }

    @PostMapping("/bonentrees")
    public BonEntree addBonEntree(@RequestBody BonEntree be) {
        return service.saveBonEntree(be);
    }

    @DeleteMapping("/bonentrees/{id}")
    public void deleteBonEntree(@PathVariable String id) {
        service.deleteBonEntree(id);
    }

    @PutMapping("/bonentrees/{id}")
    public BonEntree updateBonEntree(@PathVariable String id, @RequestBody BonEntree be) {
        be.setNumBonEntree(id);
        return service.saveBonEntree(be);
    }

    // ---- BON SORTIE ----
    @GetMapping("/bonsorties")
    public List<BonSortie> getAllBonSorties() {
        return service.getAllBonSorties();
    }

    @PostMapping("/bonsorties")
    public BonSortie addBonSortie(@RequestBody BonSortie bs) {
        return service.saveBonSortie(bs);
    }

    @DeleteMapping("/bonsorties/{id}")
    public void deleteBonSortie(@PathVariable String id) {
        service.deleteBonSortie(id);
    }

    @PutMapping("/bonsorties/{id}")
    public BonSortie updateBonSortie(@PathVariable String id, @RequestBody BonSortie bs) {
        bs.setNumBonSortie(id);
        return service.saveBonSortie(bs);
    }

    // ---- MOUVEMENTS D'UN PRODUIT ----
    @GetMapping("/produits/{id}/entrees")
    public List<BonEntree> getEntrees(@PathVariable String id) {
        return service.getEntreesByProduit(id);
    }

    @GetMapping("/produits/{id}/sorties")
    public List<BonSortie> getSorties(@PathVariable String id) {
        return service.getSortiesByProduit(id);
    }
}
