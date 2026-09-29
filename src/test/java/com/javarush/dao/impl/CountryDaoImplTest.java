package com.javarush.dao.impl;

import com.javarush.dao.BaseDaoTest;
import com.javarush.dao.CountryDao;
import com.javarush.domain.Continent;
import com.javarush.domain.Country;
import com.javarush.domain.CountryLanguage;
import org.hibernate.Session;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class CountryDaoImplTest extends BaseDaoTest {
    private CountryDao countryDao;
    private Session session;

    @BeforeEach
    void setUp() {
        countryDao = new CountryDaoImpl(sessionFactory);
        session = sessionFactory.getCurrentSession();
        session.beginTransaction();
    }

    @AfterEach
    void tearDown() {
        if (session.getTransaction().isActive()) {
            session.getTransaction().rollback();
        }
    }

    @Test
    @DisplayName("getAll() should return all countries with their languages successfully")
    void should_ReturnAllCountriesWithLanguages_Successfully() {
        Country country = new Country();
        country.setCode("UKR");
        country.setAlternativeCode("UA");
        country.setName("Ukraine");
        country.setContinent(Continent.EUROPE);
        country.setRegion("Eastern Europe");
        country.setSurfaceArea(BigDecimal.valueOf(603628));
        country.setLocalName("Ukraine");
        country.setPopulation(39500000);
        country.setGovernmentForm("Republic");
        session.persist(country);
        CountryLanguage language1 = createCountryLanguage("Ukrainian", true, BigDecimal.valueOf(81.1), country);
        CountryLanguage language2 = createCountryLanguage("English", false, BigDecimal.valueOf(4.5), country);
        session.persist(language1);
        session.persist(language2);
        country.setLanguages(Set.of(language1, language2));
        session.flush();
        session.clear();
        List<Country> actualCountries = countryDao.getAll();
        assertNotNull(actualCountries);
        assertEquals(1, actualCountries.size());
        Country actualCountry = actualCountries.get(0);
        assertEquals("Ukraine", actualCountry.getName());
        Set<CountryLanguage> languages = actualCountry.getLanguages();
        assertEquals(2, languages.size());
        boolean hasUkrainian = languages.stream().anyMatch(l -> l.getLanguage().equals("Ukrainian"));
        assertTrue(hasUkrainian);
    }

    @Test
    @DisplayName("getAll() should return country even if it has zero languages")
    void should_ReturnCountry_When_ItHasNoLanguages() {
        Country country = new Country();
        country.setCode("POL");
        country.setAlternativeCode("PL");
        country.setName("Poland");
        country.setLocalName("Poland");
        country.setContinent(Continent.EUROPE);
        country.setRegion("Eastern Europe");
        country.setSurfaceArea(BigDecimal.valueOf(312696));
        country.setPopulation(38000000);
        country.setGovernmentForm("Republic");
        country.setLanguages(Set.of());
        session.persist(country);
        session.flush();
        session.clear();
        List<Country> actualCountries = countryDao.getAll();
        assertNotNull(actualCountries);
        assertEquals(1, actualCountries.size());
        assertEquals("Poland", actualCountries.get(0).getName());
        assertTrue(actualCountries.get(0).getLanguages().isEmpty());
    }

    private CountryLanguage createCountryLanguage(String name, boolean isOfficial, BigDecimal percentage, Country country) {
        CountryLanguage language = new CountryLanguage();
        language.setLanguage(name);
        language.setIsOfficial(isOfficial);
        language.setPercentage(percentage);
        language.setCountry(country);
        return language;
    }
}
