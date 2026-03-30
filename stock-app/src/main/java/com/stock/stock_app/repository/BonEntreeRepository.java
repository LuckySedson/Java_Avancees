package com.stock.stock_app.repository;

import com.stock.stock_app.model.BonEntree;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BonEntreeRepository extends JpaRepository<BonEntree, String> {
    List<BonEntree> findByNumProduit(String numProduit);
}
