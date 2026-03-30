package com.stock.stock_app.repository;

import com.stock.stock_app.model.BonSortie;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BonSortieRepository extends JpaRepository<BonSortie, String> {
    List<BonSortie> findByNumProduit(String numProduit);
}
