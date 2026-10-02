package com.javarush.util;

import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;

public class RedisUtil {
    private RedisUtil() {
    }

    private static final String REDIS_URL = "redis://localhost:6379";
    private static RedisClient redisClient;
    private static StatefulRedisConnection<String, String> connection;

    public static RedisCommands<String, String> getCommands() {
        if (redisClient == null) {
            redisClient = RedisClient.create(REDIS_URL);
            connection = redisClient.connect();
        }
        return connection.sync();
    }

    public static void shutdown() {
        if (connection != null) connection.close();
        if (redisClient != null) redisClient.shutdown();
    }
}
