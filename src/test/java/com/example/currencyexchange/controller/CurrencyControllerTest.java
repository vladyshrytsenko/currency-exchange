package com.example.currencyexchange.controller;

import com.example.currencyexchange.model.entity.Currency;
import com.example.currencyexchange.service.CurrencyService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(CurrencyController.class)
@AutoConfigureMockMvc
class CurrencyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CurrencyService currencyService;

    @Test
    void shouldReturnListOnGettingAllCurrencies() throws Exception {
        List<Currency> currencies = List.of(
            new Currency(1L, "USD", "US Dollar", LocalDateTime.now()),
            new Currency(2L, "EUR", "Euro", LocalDateTime.now())
        );

        when(this.currencyService.getAllCurrencies()).thenReturn(currencies);

        this.mockMvc.perform(get("/api/v1/currencies"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.size()").value(2))
            .andExpect(jsonPath("$[0].code").value("USD"))
            .andExpect(jsonPath("$[1].code").value("EUR"));
    }

    @Test
    void shouldReturnCreatedCurrency() throws Exception {
        LocalDateTime createdAt = LocalDateTime.now();
        Currency returnedCurrency = new Currency(1L, "JPY", "Japanese Yen", createdAt);

        when(this.currencyService.addCurrency(any())).thenReturn(returnedCurrency);

        this.mockMvc.perform(post("/api/v1/currencies")
                .contentType(MediaType.APPLICATION_JSON)
                .content(String.format("""
                    {
                        "code": "JPY",
                        "name": "Japanese Yen",
                        "createdAt": "%s"
                    }
                """, createdAt)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.code").value("JPY"))
            .andExpect(jsonPath("$.name").value("Japanese Yen"));
    }

    @Test
    void shouldReturnExchangeRates() throws Exception {
        Map<String, BigDecimal> rates = Map.of("EUR", BigDecimal.valueOf(0.9), "GBP", BigDecimal.valueOf(0.8));

        when(this.currencyService.getRatesForCurrency("USD")).thenReturn(rates);

        this.mockMvc.perform(get("/api/v1/currencies/USD/rate"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.EUR").value(0.9))
            .andExpect(jsonPath("$.GBP").value(0.8));
    }
}
