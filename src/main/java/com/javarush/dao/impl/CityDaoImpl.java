package com.javarush.dao.impl;

import com.javarush.dao.CityDao;
import com.javarush.domain.City;
import com.javarush.domain.Page;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

import java.util.ArrayList;
import java.util.List;

public class CityDaoImpl implements CityDao {
    private final SessionFactory sessionFactory;

    public CityDaoImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public List<City> getItems(Page page) {
        return sessionFactory.getCurrentSession()
                .createQuery("select distinct c from City c join fetch c.country co join fetch co.languages",
                        City.class)
                .setFirstResult(page.offset())
                .setMaxResults(page.limit())
                .list();
    }

    @Override
    public int getTotalCount() {
        Long count = sessionFactory.getCurrentSession()
                .createQuery("select count(c) from City c", Long.class)
                .uniqueResult();
        return count != null ? count.intValue() : 0;
    }

    @Override
    public City getById(Integer id) {
        Query<City> query = sessionFactory.getCurrentSession()
                .createQuery("select c from City c join fetch c.country where c.id = :ID", City.class);
        query.setParameter("ID", id);
        return query.getSingleResult();
    }

    @Override
    public List<City> getByIds(List<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            return new ArrayList<>();
        }
        return sessionFactory.getCurrentSession()
                .createQuery("select c from City c join fetch c.country where c.id in (:IDS)",
                        City.class)
                .setParameter("IDS", ids)
                .list();
    }
}
