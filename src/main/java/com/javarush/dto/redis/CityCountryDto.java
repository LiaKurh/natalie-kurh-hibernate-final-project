package com.javarush.dto.redis;

import com.javarush.domain.Continent;

import java.math.BigDecimal;
import java.util.Set;

public record CityCountryDto(
        Integer id,
        String name,
        String district,
        Integer population,
        String countryCode,
        String alternativeCountryCode,
        String countryName,
        Continent continent,
        String countryRegion,
        BigDecimal countrySurfaceArea,
        Integer countryPopulation,
        Set<LanguageDto> languages
) {
}
