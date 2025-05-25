package com.example.currencyexchange.repository;

import com.example.currencyexchange.model.entity.Currency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CurrencyRepository extends JpaRepository<Currency, String> {

    boolean existsByCode(String code);
}
