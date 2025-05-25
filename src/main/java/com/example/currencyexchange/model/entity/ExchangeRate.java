package com.example.currencyexchange.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity(name = "exchange_rates")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExchangeRate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "base_currency_code", length = 3)
    private String baseCurrencyCode;

    @Column(name = "target_currency_code", length = 3)
    private String targetCurrencyCode;

    @Column(precision = 19, scale = 6)
    private BigDecimal rate;
}
