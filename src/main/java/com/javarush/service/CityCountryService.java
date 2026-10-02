package com.javarush.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.javarush.domain.City;
import com.javarush.domain.Country;
import com.javarush.domain.CountryLanguage;
import com.javarush.dto.redis.CityCountryDto;
import com.javarush.dto.redis.LanguageDto;
import com.javarush.exception.RedisDataException;
import io.lettuce.core.KeyValue;
import io.lettuce.core.api.sync.RedisCommands;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class CityCountryService {
    private final ObjectMapper objectMapper;
    private final RedisCommands<String, String> redis;

    public CityCountryService(RedisCommands<String, String> redis) {
        this.objectMapper = new ObjectMapper();
        this.redis = redis;
    }

    public List<CityCountryDto> transformDataToDto(List<City> cities) {
        return cities.stream().map(city -> {
            Country country = city.getCountry();
            Set<LanguageDto> languages = transformLanguageToDto(country.getLanguages());
            return new CityCountryDto(
                    city.getId(),
                    city.getName(),
                    city.getDistrict(),
                    city.getPopulation(),
                    country.getCode(),
                    country.getAlternativeCode(),
                    country.getName(),
                    country.getContinent(),
                    country.getRegion(),
                    country.getSurfaceArea(),
                    country.getPopulation(),
                    languages);
        }).collect(Collectors.toList());
    }

    public void pushToRedis(List<CityCountryDto> cityCountryDtos) {
        try {
            for (CityCountryDto dto : cityCountryDtos) {
                String json = objectMapper.writeValueAsString(dto);
                redis.set(String.valueOf(dto.id()), json);
            }
        } catch (JsonProcessingException e) {
            throw new RedisDataException("Can't push data to redis! Serialization error!", e);
        }
    }

    public List<CityCountryDto> getFromRedis(List<Integer> ids) {
        List<CityCountryDto> dtos = new ArrayList<>();
        String[] keys = ids.stream()
                .map(String::valueOf)
                .toArray(String[]::new);
        try {
            List<KeyValue<String, String>> results = redis.mget(keys);
            for (KeyValue<String, String> kv : results) {
                if (kv.hasValue()) {
                    CityCountryDto dto = objectMapper.readValue(kv.getValue(), CityCountryDto.class);
                    dtos.add(dto);
                }
            }
            return dtos;
        } catch (JsonProcessingException e) {
            throw new RedisDataException("Can't get data from redis! Deserialization error!", e);
        }
    }

    private Set<LanguageDto> transformLanguageToDto(Set<CountryLanguage> languages) {
        return languages.stream()
                .map(language -> new LanguageDto(
                        language.getLanguage(),
                        language.getIsOfficial(),
                        language.getPercentage()
                ))
                .collect(Collectors.toSet());
    }
}
