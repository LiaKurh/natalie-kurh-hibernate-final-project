package com.javarush;

import com.javarush.dao.CityDao;
import com.javarush.dao.impl.CityDaoImpl;
import com.javarush.domain.City;
import com.javarush.dto.redis.CityCountryDto;
import com.javarush.service.CityCountryService;
import com.javarush.service.CityService;
import com.javarush.util.HibernateUtil;
import com.javarush.util.RedisUtil;
import com.javarush.util.TransactionManager;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class Main {
    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        try (HibernateUtil hibernateUtil = new HibernateUtil()) {
            SessionFactory sessionFactory = hibernateUtil.getSessionFactory();
            TransactionManager transactionManager = new TransactionManager(sessionFactory);

            CityDao cityDao = new CityDaoImpl(sessionFactory);
            CityService cityService = new CityService(cityDao, transactionManager);
            CityCountryService cityCountryService = new CityCountryService(RedisUtil.getCommands());
            log.info("=== STAGE 1: FULL DATA LOAD & CACHING ===");
            long startMysql = System.currentTimeMillis();
            List<City> cities = cityService.fetchData();
            long endMysql = System.currentTimeMillis();
            log.info("MySQL FULL LOAD ({} items): {} ms", cities.size(), (endMysql - startMysql));

            List<CityCountryDto> cityCountryDtos = cityCountryService.transformDataToDto(cities);
            cityCountryService.pushToRedis(cityCountryDtos);
            List<Integer> allIds = cityCountryDtos.stream().map(CityCountryDto::id).toList();
            long startRedis = System.currentTimeMillis();
            List<CityCountryDto> cachedDtos = cityCountryService.getFromRedis(allIds);
            long endRedis = System.currentTimeMillis();
            log.info("Redis FULL LOAD ({} items): {} ms", cachedDtos.size(), (endRedis - startRedis));

            log.info("=== STAGE 2: TARGETED SELECTION BY IDS ===");
            List<Integer> ids = List.of(3, 2545, 123, 4, 189, 89, 3458, 1189, 10, 102);
            long startTargetRedis = System.currentTimeMillis();
            List<CityCountryDto> targetRedisDtos = cityCountryService.getFromRedis(ids);
            long endTargetRedis = System.currentTimeMillis();
            log.info("Redis TARGET LOAD ({} items): {} ms", targetRedisDtos.size(), (endTargetRedis - startTargetRedis));
            long startTargetMysql = System.currentTimeMillis();
            List<City> targetMysqlCities = cityService.getByIds(ids);
            long endTargetMysql = System.currentTimeMillis();
            log.info("MySQL TARGET LOAD ({} items): {} ms", targetMysqlCities.size(), (endTargetMysql - startTargetMysql));
            log.info("=========================================");
        } catch (Exception e) {
            log.error("An error occurred during execution", e);
        } finally {
            RedisUtil.shutdown();
        }
    }
}
