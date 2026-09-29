package com.javarush.dao;

import com.javarush.domain.City;
import com.javarush.domain.Country;
import com.javarush.domain.CountryLanguage;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public abstract class BaseDaoTest {
    protected static SessionFactory sessionFactory;

    @BeforeAll
    static void beforeAll() throws IOException {
        Properties properties = new Properties();
        try (InputStream inputStream = BaseDaoTest.class.getClassLoader()
                .getResourceAsStream("hibernate-test.properties")) {
            properties.load(inputStream);
        }
        sessionFactory = new Configuration()
                .addProperties(properties)
                .addAnnotatedClass(City.class)
                .addAnnotatedClass(Country.class)
                .addAnnotatedClass(CountryLanguage.class)
                .buildSessionFactory();
    }

    @AfterAll
    static void afterAll() {
        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }
}
