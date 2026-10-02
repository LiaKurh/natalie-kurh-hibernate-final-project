package com.javarush.dao;

import com.javarush.domain.Country;

import java.util.List;

public interface CountryDao {

    List<Country> getAll();
}
