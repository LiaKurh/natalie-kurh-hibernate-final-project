package com.javarush.service;

import com.javarush.domain.City;
import com.javarush.domain.Continent;
import com.javarush.domain.Country;
import com.javarush.domain.CountryLanguage;
import com.javarush.dto.redis.CityCountryDto;
import com.javarush.dto.redis.LanguageDto;
import com.javarush.exception.RedisDataException;
import io.lettuce.core.KeyValue;
import io.lettuce.core.api.sync.RedisCommands;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CityCountryServiceTest {
    @Mock
    private RedisCommands<String, String> redis;
    @InjectMocks
    private CityCountryService cityCountryService;
    private City city;

    @BeforeEach
    void setUp() {
        CountryLanguage countryLanguage = new CountryLanguage();
        countryLanguage.setLanguage("ukrainian");
        countryLanguage.setIsOfficial(true);
        countryLanguage.setPercentage(BigDecimal.valueOf(81.1));

        Country country = new Country();
        country.setCode("UKR");
        country.setAlternativeCode("UA");
        country.setName("Ukraine");
        country.setContinent(Continent.EUROPE);
        country.setRegion("Eastern Europe");
        country.setSurfaceArea(BigDecimal.valueOf(603628));
        country.setPopulation(39500000);
        country.setLanguages(Set.of(countryLanguage));

        city = new City();
        city.setId(1);
        city.setName("Kyiv");
        city.setDistrict("Kyivska oblast");
        city.setPopulation(2800000);
        city.setCountry(country);
    }

    @Test
    @DisplayName("Should successfully transform data models into DTOs")
    void should_TransformDataToDto_Successfully() {
        List<CityCountryDto> dtos = cityCountryService.transformDataToDto(List.of(city));
        assertNotNull(dtos);
        assertEquals(1, dtos.size());

        CityCountryDto dto = dtos.get(0);
        assertEquals(1, dto.id());
        assertEquals("Kyiv", dto.name());
        assertEquals("Ukraine", dto.countryName());
        assertEquals(Continent.EUROPE, dto.continent());

        Set<LanguageDto> languages = dto.languages();
        assertEquals(1, languages.size());
        LanguageDto langDto = languages.iterator().next();
        assertEquals("ukrainian", langDto.language());
        assertTrue(langDto.isOfficial());
    }

    @Test
    @DisplayName("pushToRedis() should successfully push data to Redis")
    void should_PushDataToRedis_Successfully() {
        LanguageDto languageDto = new LanguageDto("ukrainian", true, BigDecimal.valueOf(81.1));
        CityCountryDto cityCountryDto = new CityCountryDto(
                1,
                "Kyiv",
                "Kyivska oblast",
                2800000,
                "UKR",
                "UA",
                "Ukraine",
                Continent.EUROPE,
                "Eastern Europe",
                BigDecimal.valueOf(603628),
                39500000,
                Set.of(languageDto)
        );
        List<CityCountryDto> dtos = List.of(cityCountryDto);
        cityCountryService.pushToRedis(dtos);
        verify(redis, times(1)).set(eq("1"), anyString());
    }

    @Test
    @DisplayName("getFromRedis() should successfully get and parse data from redis")
    void should_GetFromRedis_Successfully() {
        String mockJson = "{\"id\":1,\"name\":\"Kyiv\",\"district\":\"Kyivska oblast\",\"population\":2800000," +
                "\"countryCode\":\"UKR\",\"alternativeCountryCode\":\"UA\",\"countryName\":\"Ukraine\"," +
                "\"continent\":\"EUROPE\",\"countryRegion\":\"Eastern Europe\",\"countrySurfaceArea\":603628," +
                "\"countryPopulation\":39500000,\"languages\":[{\"language\":\"ukrainian\",\"isOfficial\":true,\"percentage\":81.1}]}";
        when(redis.mget(new String[]{"1"})).thenReturn(List.of(KeyValue.just("1", mockJson)));

        List<CityCountryDto> result = cityCountryService.getFromRedis(List.of(1));
        assertNotNull(result);
        assertEquals(1, result.size());

        CityCountryDto dto = result.get(0);
        assertEquals("Kyiv", dto.name());
        assertEquals("Kyivska oblast", dto.district());
        assertEquals(2800000, dto.population());
        assertEquals(Continent.EUROPE, dto.continent());
        assertEquals(new BigDecimal("603628"), dto.countrySurfaceArea());
        assertEquals("Ukraine", dto.countryName());

        Set<LanguageDto> languageDtos = dto.languages();
        assertEquals(1, languageDtos.size());

        LanguageDto languageDto = languageDtos.iterator().next();
        assertEquals("ukrainian", languageDto.language());
        assertTrue(languageDto.isOfficial());
        assertEquals(new BigDecimal("81.1"), languageDto.percentage());
    }

    @Test
    @DisplayName("getFromRedis() should throw RedisDataException when json data is corrupted")
    void should_ThrowRedisDataException_When_JsonIsCorrupted() {
        when(redis.mget("1")).thenReturn(List.of(KeyValue.just("1", "invalid.json")));
        RedisDataException exception = assertThrows(RedisDataException.class, () ->
                cityCountryService.getFromRedis(List.of(1)));
        assertEquals("Can't get data from redis! Deserialization error!", exception.getMessage());
    }
}
