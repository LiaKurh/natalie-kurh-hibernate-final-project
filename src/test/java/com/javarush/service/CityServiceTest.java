package com.javarush.service;

import com.javarush.dao.CityDao;
import com.javarush.domain.City;
import com.javarush.domain.Page;
import com.javarush.util.TransactionManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CityServiceTest {
    @Mock
    private CityDao cityDao;
    @Mock
    private TransactionManager transactionManager;
    @InjectMocks
    private CityService cityService;

    @BeforeEach
    void setUp() {
        lenient().when(transactionManager.runInTransaction(any(Supplier.class)))
                .thenAnswer(invocation -> {
                    Supplier<?> action = invocation.getArgument(0);
                    return action.get();
                });
    }

    @Test
    @DisplayName("fetchData() should fetch all cities page by page correctly")
    void should_FetchAllCitiesPageByPage_Successfully() {
        int totalCount = 1200;
        when(cityDao.getTotalCount()).thenReturn(totalCount);
        List<City> page1 = createCities(500);
        List<City> page2 = createCities(500);
        List<City> page3 = createCities(200);
        when(cityDao.getItems(new Page(0, 500))).thenReturn(page1);
        when(cityDao.getItems(new Page(500, 500))).thenReturn(page2);
        when(cityDao.getItems(new Page(1000, 500))).thenReturn(page3);
        List<City> result = cityService.fetchData();
        assertNotNull(result);
        assertEquals(1200, result.size());
        verify(cityDao, times(1)).getTotalCount();
        verify(cityDao, times(3)).getItems(any(Page.class));
        verify(transactionManager, times(1)).runInTransaction(any(Supplier.class));
    }

    @Test
    @DisplayName("fetchData() should return empty list when database has zero cities")
    void should_ReturnEmptyList_When_NoCitiesInDatabase() {
        when(cityDao.getTotalCount()).thenReturn(0);
        List<City> result = cityService.fetchData();
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(cityDao, times(1)).getTotalCount();
        verify(cityDao, never()).getItems(any(Page.class));
    }

    @Test
    @DisplayName("getByIds() should return requested cities from DAO successfully")
    void should_ReturnRequestedCities_When_ValidIdsPassed() {
        List<Integer> ids = List.of(1, 2, 3);
        City city1 = new City();
        city1.setId(1);
        city1.setName("Kyiv");
        City city2 = new City();
        city2.setId(2);
        city2.setName("Lviv");
        List<City> expectedCities = List.of(city1, city2);
        when(cityDao.getByIds(ids)).thenReturn(expectedCities);
        List<City> actualCities = cityService.getByIds(ids);
        assertNotNull(actualCities);
        assertEquals(2, actualCities.size());
        assertEquals("Kyiv", actualCities.get(0).getName());
        assertEquals("Lviv", actualCities.get(1).getName());
        verify(cityDao, times(1)).getByIds(ids);
        verify(transactionManager, times(1)).runInTransaction(any(Supplier.class));
    }

    private List<City> createCities(int count) {
        List<City> list = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            list.add(new City());
        }
        return list;
    }
}
