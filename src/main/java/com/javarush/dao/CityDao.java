package com.javarush.dao;

import com.javarush.domain.City;
import com.javarush.domain.Page;

import java.util.List;

public interface CityDao {

    City getById(Integer id);

    List<City> getByIds(List<Integer> ids);

    List<City> getItems(Page page);

    int getTotalCount();
}
