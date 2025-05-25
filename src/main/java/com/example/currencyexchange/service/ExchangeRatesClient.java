package com.example.currencyexchange.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Map;

import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ExchangeRatesClient {

    public Optional<Map<String, BigDecimal>> getLatestRates(String code) {
        try {
            String url = String.format("%s/latest.json?app_id=%s&base=%s", baseUrl, apiKey, code);

            Map<String, Object> response = this.webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                .block(Duration.ofSeconds(10));

            Map<String, Object> ratesMap = (Map<String, Object>) response.get("rates");
            if (ratesMap == null) return Optional.empty();

            Map<String, BigDecimal> rates = ratesMap.entrySet().stream()
                .collect(Collectors.toMap(
                    Map.Entry::getKey,
                    e -> new BigDecimal(e.getValue().toString())
                ));

            return Optional.of(rates);

        } catch (Exception e) {
            log.error("Failed to fetch exchange rates", e);
            return Optional.empty();
        }
    }

    private final WebClient webClient;

    @Value("${exchange-rates.api-key}")
    private String apiKey;

    @Value("${exchange-rates.base-url}")
    private String baseUrl;

}
