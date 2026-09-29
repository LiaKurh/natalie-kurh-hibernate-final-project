package com.javarush.service;

import com.javarush.dao.CountryDao;
import com.javarush.domain.Continent;
import com.javarush.domain.Country;
import com.javarush.exception.DataProcessingException;
import com.javarush.util.TransactionManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CountryServiceTest {
    @Mock
    private CountryDao countryDao;
    @Mock
    private TransactionManager transactionManager;
    @InjectMocks
    private CountryService countryService;

    @BeforeEach
    void setUp() {
        lenient().when(transactionManager.runInTransaction(any(Supplier.class)))
                .thenAnswer(invocation -> {
                    Supplier<?> action = invocation.getArgument(0);
                    return action.get();
                });
    }

    @Test
    @DisplayName("getAll() should return all countries from DAO successfully")
    void should_ReturnAllCountries_Successfully() {
        Country country1 = new Country();
        country1.setCode("UKR");
        country1.setName("Ukraine");
        country1.setContinent(Continent.EUROPE);
        Country country2 = new Country();
        country2.setCode("JP");
        country2.setName("Japan");
        country2.setContinent(Continent.ASIA);
        List<Country> expectedCountries = List.of(country1, country2);
        when(countryDao.getAll()).thenReturn(expectedCountries);
        List<Country> actualCountries = countryService.getAll();
        assertNotNull(actualCountries);
        assertEquals(2, actualCountries.size());
        assertEquals("Ukraine", actualCountries.get(0).getName());
        assertEquals("UKR", actualCountries.get(0).getCode());
        assertEquals("Japan", actualCountries.get(1).getName());
        assertEquals("JP", actualCountries.get(1).getCode());
        verify(countryDao, times(1)).getAll();
        verify(transactionManager, times(1)).runInTransaction(any(Supplier.class));
    }

    @Test
    @DisplayName("getAll() should throw DataProcessingException when DAO fails")
    void should_ThrowDataProcessingException_When_DaoFails() {
        when(transactionManager.runInTransaction(any(Supplier.class)))
                .thenThrow(new RuntimeException("Database connection lost"));
        DataProcessingException exception = assertThrows(DataProcessingException.class, () ->
                countryService.getAll());
        assertEquals("Can't get all countries!", exception.getMessage());
        assertNotNull(exception.getCause());
        assertEquals("Database connection lost", exception.getCause().getMessage());
    }
}
