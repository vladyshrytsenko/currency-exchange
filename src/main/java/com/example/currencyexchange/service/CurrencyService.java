package com.example.currencyexchange.service;

import com.example.currencyexchange.model.dto.CurrencyDto;
import com.example.currencyexchange.model.entity.Currency;
import com.example.currencyexchange.model.entity.ExchangeRate;
import com.example.currencyexchange.repository.CurrencyRepository;
import com.example.currencyexchange.repository.ExchangeRateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static com.example.currencyexchange.model.dto.CurrencyDto.toEntity;

@Service
@RequiredArgsConstructor
@Slf4j
public class CurrencyService {

    @Scheduled(
        fixedRateString = "${exchange-rates.fetch-interval}",
        initialDelayString = "${exchange-rates.initial-delay}"
    )
    public void scheduledRatesUpdate() {
        List<Currency> currencies = this.currencyRepository.findAll();
        currencies.forEach(currency ->
            this.fetchAndStoreCurrencyRates(currency.getCode())
        );
    }

    public List<Currency> getAllCurrencies() {
        return this.currencyRepository.findAll();
    }

    @Transactional
    public Currency addCurrency(CurrencyDto currencyDto) {
        if (this.currencyRepository.existsByCode(currencyDto.getCode())) {
            throw new RuntimeException("Currency already exists");
        }

        Currency currency = this.currencyRepository.save(toEntity(currencyDto));

        this.fetchAndStoreCurrencyRates(currency.getCode());
        return currency;
    }

    public Map<String, BigDecimal> getRatesForCurrency(String currencyCode) {
        if (!ratesCache.containsKey(currencyCode)) {
            throw new RuntimeException("Rates not available for currency: " + currencyCode);
        }
        return ratesCache.get(currencyCode);
    }

    private void fetchAndStoreCurrencyRates(String currencyCode) {
        this.exchangeRatesClient.getLatestRates(currencyCode)
            .ifPresent(rates -> {
                ratesCache.put(currencyCode, rates);
                this.saveRatesToDatabase(currencyCode, rates);
            });
    }

    private void saveRatesToDatabase(String currencyCode, Map<String, BigDecimal> rates) {
        List<ExchangeRate> ratesToSave = rates.entrySet().stream()
            .filter(entry -> currencyRepository.existsByCode(entry.getKey()))
            .map(entry -> ExchangeRate.builder()
                .baseCurrencyCode(currencyCode)
                .targetCurrencyCode(entry.getKey())
                .rate(entry.getValue())
                .build())
            .collect(Collectors.toList());

        this.exchangeRateRepository.saveAll(ratesToSave);
    }

    private final CurrencyRepository currencyRepository;
    private final ExchangeRateRepository exchangeRateRepository;
    private final ExchangeRatesClient exchangeRatesClient;

    private final Map<String, Map<String, BigDecimal>> ratesCache = new ConcurrentHashMap<>();
}
