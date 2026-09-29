package com.javarush.exception;

public class RedisDataException extends RuntimeException {

    public RedisDataException(String message, Throwable cause) {
        super(message, cause);
    }
}
