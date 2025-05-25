package com.example.currencyexchange.model.dto;

import com.example.currencyexchange.model.entity.Currency;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CurrencyDto {

    @NotBlank
    @Size(min = 3, max = 3)
    private String code;

    @NotBlank
    private String name;

    private LocalDateTime createdAt;

    public static Currency toEntity(CurrencyDto dto) {
        return Currency.builder()
            .code(dto.getCode())
            .name(dto.getName())
            .createdAt(LocalDateTime.now())
            .build();
    }

    public static CurrencyDto toDto(Currency entity) {
        return CurrencyDto.builder()
            .code(entity.getCode())
            .name(entity.getName())
            .createdAt(entity.getCreatedAt())
            .build();
    }

    public static List<CurrencyDto> toDtoList(List<Currency> list) {
        if (list == null || list.isEmpty()) {
            return List.of();
        }

        return list.stream()
            .map(CurrencyDto::toDto)
            .collect(Collectors.toList());
    }
}
