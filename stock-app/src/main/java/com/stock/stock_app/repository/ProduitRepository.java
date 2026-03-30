package com.stock.stock_app.repository;

import com.stock.stock_app.model.Produit;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProduitRepository extends JpaRepository<Produit, String> {
    List<Produit> findByDesignContainingIgnoreCase(String design);
}