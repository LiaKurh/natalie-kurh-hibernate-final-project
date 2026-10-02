package com.javarush.dao.impl;

import com.javarush.dao.BaseDaoTest;
import com.javarush.dao.CityDao;
import com.javarush.domain.*;
import org.hibernate.Session;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class CityDaoImplTest extends BaseDaoTest {
    private CityDao cityDao;
    private Session session;

    @BeforeEach
    void setUp() {
        cityDao = new CityDaoImpl(sessionFactory);
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
    @DisplayName("getTotalCount() should return correct number of rows from H2 database")
    void should_ReturnCorrectTotalCount() {
        Country country = createCountry("UKR", "Ukraine");
        session.persist(country);
        City city1 = createCity("Kyiv", country);
        City city2 = createCity("Lviv", country);
        session.persist(city1);
        session.persist(city2);
        session.flush();
        int totalCount = cityDao.getTotalCount();
        assertEquals(2, totalCount);
    }

    @Test
    @DisplayName("getItems() should load cities with pagination limits and offsets")
    void should_ReturnCitiesWithPagination() {
        Country country = createCountry("UKR", "Ukraine");
        session.persist(country);
        CountryLanguage countryLanguage = createCountryLanguage(country);
        session.persist(countryLanguage);
        country.setLanguages(Set.of(countryLanguage));
        for (int i = 1; i <= 5; i++) {
            session.persist(createCity("City " + i, country));
        }
        session.flush();

        Page page = new Page(2, 2);
        List<City> actualCities = cityDao.getItems(page);
        assertNotNull(actualCities);
        assertEquals(2, actualCities.size());
        assertEquals("City 3", actualCities.get(0).getName());
        assertEquals("City 4", actualCities.get(1).getName());
    }

    @Test
    @DisplayName("getById() should return correct city with fetch joined country")
    void should_ReturnCorrectCity_When_ValidIdPassed() {
        Country country = createCountry("UKR", "Ukraine");
        session.persist(country);
        City expectedCity = createCity("Kyiv", country);
        session.persist(expectedCity);
        session.flush();
        session.clear();
        int id = expectedCity.getId();
        City actualCity = cityDao.getById(id);
        assertNotNull(actualCity);
        assertEquals(expectedCity.getId(), actualCity.getId());
        assertEquals("Kyiv", actualCity.getName());
        assertNotNull(actualCity.getCountry());
        assertEquals("Ukraine", actualCity.getCountry().getName());
    }

    @Test
    @DisplayName("getByIds() should return only matching cities from the database using IN operator")
    void should_ReturnRequestedCities_When_ListOfIdsPassed() {
        Country country = createCountry("UKR", "Ukraine");
        session.persist(country);
        City kyiv = createCity("Kyiv", country);
        session.persist(kyiv);
        City lviv = createCity("Lviv", country);
        session.persist(lviv);
        City odesa = createCity("Odesa", country);
        session.persist(odesa);
        session.flush();
        session.clear();
        List<Integer> requestedIds = List.of(kyiv.getId(), odesa.getId());
        List<City> actualCities = cityDao.getByIds(requestedIds);
        assertNotNull(actualCities);
        assertEquals(2, actualCities.size());
        assertEquals("Kyiv", actualCities.get(0).getName());
        assertEquals("Odesa", actualCities.get(1).getName());
        boolean hasLviv = actualCities.stream().anyMatch(c -> c.getName().equals("Lviv"));
        assertFalse(hasLviv);
    }

    @Test
    @DisplayName("getByIds() should return empty list when empty list of IDs passed")
    void should_ReturnEmptyList_When_EmptyListOfIdsPassed() {
        List<Integer> emptyIds = Collections.emptyList();
        List<City> actualCities = cityDao.getByIds(emptyIds);
        assertNotNull(actualCities);
        assertTrue(actualCities.isEmpty());
    }

    @Test
    @DisplayName("getByIds() should return empty list when null passed instead of IDs")
    void should_ReturnEmptyList_When_NullPassedInsteadOfIds() {
        List<City> actualCities = cityDao.getByIds(null);
        assertNotNull(actualCities);
        assertTrue(actualCities.isEmpty());
    }

    private Country createCountry(String code, String name) {
        Country country = new Country();
        country.setCode(code);
        country.setAlternativeCode(code.substring(0, 2));
        country.setName(name);
        country.setLocalName("Test Local Name");
        country.setGovernmentForm("Test Government Form");
        country.setContinent(Continent.EUROPE);
        country.setRegion("Test Region");
        country.setSurfaceArea(BigDecimal.TEN);
        country.setPopulation(100);
        return country;
    }

    private City createCity(String name, Country country) {
        City city = new City();
        city.setName(name);
        city.setDistrict("Test District");
        city.setPopulation(50);
        city.setCountry(country);
        return city;
    }

    private CountryLanguage createCountryLanguage(Country country) {
        CountryLanguage countryLanguage = new CountryLanguage();
        countryLanguage.setLanguage("Test language");
        countryLanguage.setIsOfficial(true);
        countryLanguage.setPercentage(BigDecimal.valueOf(90));
        countryLanguage.setCountry(country);
        return countryLanguage;
    }
}
