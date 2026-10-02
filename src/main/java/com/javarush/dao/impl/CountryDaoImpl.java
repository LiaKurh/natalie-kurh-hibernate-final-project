package com.javarush.dao.impl;

import com.javarush.dao.CountryDao;
import com.javarush.domain.Country;
import org.hibernate.SessionFactory;

import java.util.List;

public class CountryDaoImpl implements CountryDao {
    private final SessionFactory sessionFactory;

    public CountryDaoImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public List<Country> getAll() {
        return sessionFactory.getCurrentSession()
                .createQuery("select distinct c from Country c left join fetch c.languages",
                        Country.class)
                .list();
    }
}
