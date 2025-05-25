package com.example.currencyexchange.service;

import com.example.currencyexchange.model.dto.CurrencyDto;
import com.example.currencyexchange.model.entity.Currency;
import com.example.currencyexchange.repository.CurrencyRepository;
import com.example.currencyexchange.repository.ExchangeRateRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CurrencyServiceTest {

    @Test
    void shouldReturnAllCurrencies() {
        List<Currency> currencies = List.of(
            Currency.builder().code("USD").name("US Dollar").build(),
            Currency.builder().code("EUR").name("Euro").build()
        );

        when(this.currencyRepository.findAll()).thenReturn(currencies);

        List<Currency> result = this.currencyService.getAllCurrencies();

        assertEquals(2, result.size());
        assertEquals("USD", result.get(0).getCode());
    }

    @Test
    void shouldSaveAndFetchRates() {
        CurrencyDto dto = new CurrencyDto("PLN", "Polish Zloty", LocalDateTime.now());

        when(this.currencyRepository.existsByCode("PLN")).thenReturn(false);
        when(this.currencyRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        Map<String, BigDecimal> rates = Map.of("USD", BigDecimal.valueOf(4.0));
        when(this.exchangeRatesClient.getLatestRates("PLN")).thenReturn(Optional.of(rates));
        when(this.currencyRepository.existsByCode("USD")).thenReturn(true);

        Currency result = this.currencyService.addCurrency(dto);

        assertEquals("PLN", result.getCode());
        verify(this.exchangeRateRepository).saveAll(anyList());
    }

    @Test
    void shouldThrowIfExistsOnAddingCurrency() {
        CurrencyDto dto = new CurrencyDto("EUR", "Euro", LocalDateTime.now());

        when(this.currencyRepository.existsByCode("EUR")).thenReturn(true);

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
            this.currencyService.addCurrency(dto)
        );

        assertEquals("Currency already exists", ex.getMessage());
    }

    @Test
    void shouldReturnFromCacheOnGettingRatesForCurrency() {
        String base = "USD";
        Map<String, BigDecimal> rates = Map.of("EUR", BigDecimal.valueOf(0.92));

        var ratesMap = getPrivateCache(this.currencyService);
        ratesMap.put(base, rates);
        this.currencyService.getRatesForCurrency(base);

        Map<String, BigDecimal> result = this.currencyService.getRatesForCurrency(base);
        assertEquals(BigDecimal.valueOf(0.92), result.get("EUR"));
    }

    @Test
    void shouldThrowIfNotInCacheOnGettingRatesForCurrency() {
        RuntimeException ex = assertThrows(RuntimeException.class, () ->
            this.currencyService.getRatesForCurrency("INR")
        );
        assertTrue(ex.getMessage().contains("Rates not available"));
    }

    @SuppressWarnings("unchecked")
    private ConcurrentHashMap<String, Map<String, BigDecimal>> getPrivateCache(CurrencyService service) {
        try {
            var field = CurrencyService.class.getDeclaredField("ratesCache");
            field.setAccessible(true);
            return (ConcurrentHashMap<String, Map<String, BigDecimal>>) field.get(service);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Mock
    private CurrencyRepository currencyRepository;

    @Mock
    private ExchangeRateRepository exchangeRateRepository;

    @Mock
    private ExchangeRatesClient exchangeRatesClient;

    @InjectMocks
    private CurrencyService currencyService;
}
