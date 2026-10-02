package com.javarush.service;

import com.javarush.dao.CountryDao;
import com.javarush.domain.Country;
import com.javarush.exception.DataProcessingException;
import com.javarush.util.TransactionManager;

import java.util.List;

public class CountryService {
    private final CountryDao countryDao;
    private final TransactionManager transactionManager;

    public CountryService(CountryDao countryDao, TransactionManager transactionManager) {
        this.countryDao = countryDao;
        this.transactionManager = transactionManager;
    }

    public List<Country> getAll() {
        try {
            return transactionManager.runInTransaction(countryDao::getAll);
        } catch (Exception e) {
            throw new DataProcessingException("Can't get all countries!", e);
        }
    }
}
