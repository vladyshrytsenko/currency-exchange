package com.example.currencyexchange.controller;

import com.example.currencyexchange.model.dto.CurrencyDto;
import com.example.currencyexchange.model.entity.Currency;
import com.example.currencyexchange.service.CurrencyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/currencies")
@RequiredArgsConstructor
public class CurrencyController {

    @GetMapping
    public List<CurrencyDto> getAllCurrencies() {
        List<Currency> currencies = currencyService.getAllCurrencies();
        return CurrencyDto.toDtoList(currencies);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CurrencyDto addCurrency(@RequestBody CurrencyDto currencyDto) {
        Currency currency = currencyService.addCurrency(currencyDto);
        return CurrencyDto.toDto(currency);
    }

    @GetMapping("/{code}/rate")
    public ResponseEntity<Map<String, BigDecimal>> getRate(@PathVariable String code) {
        return ResponseEntity.ok(currencyService.getRatesForCurrency(code));
    }

    private final CurrencyService currencyService;
}
