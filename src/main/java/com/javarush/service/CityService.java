package com.javarush.service;

import com.javarush.dao.CityDao;
import com.javarush.domain.City;
import com.javarush.domain.Page;
import com.javarush.exception.DataProcessingException;
import com.javarush.util.TransactionManager;

import java.util.ArrayList;
import java.util.List;

public class CityService {
    private final CityDao cityDao;
    private final TransactionManager transactionManager;

    public CityService(CityDao cityDao, TransactionManager transactionManager) {
        this.cityDao = cityDao;
        this.transactionManager = transactionManager;
    }

    public List<City> fetchData() {
        try {
            return transactionManager.runInTransaction(() -> {
                List<City> allCities = new ArrayList<>();
                int totalCount = cityDao.getTotalCount();
                int step = 500;

                for (int i = 0; i < totalCount; i += step) {
                    Page page = new Page(i, step);
                    allCities.addAll(cityDao.getItems(page));
                }
                return allCities;
            });
        } catch (Exception e) {
            throw new DataProcessingException("Can't get all cities!", e);
        }
    }

    public List<City> getByIds(List<Integer> ids) {
        return transactionManager.runInTransaction(() -> cityDao.getByIds(ids));
    }
}
