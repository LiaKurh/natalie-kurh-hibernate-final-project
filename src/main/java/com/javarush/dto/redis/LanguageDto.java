package com.javarush.dto.redis;

import java.math.BigDecimal;

public record LanguageDto(
        String language,
        Boolean isOfficial,
        BigDecimal percentage) {
}
