package com.management.inventory.stock.domain.repository;

import com.management.inventory.stock.domain.entity.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StockRepository extends JpaRepository<Stock, Long> {

    long findByProductNameAndOptionName(String productName, String optionName);

}
