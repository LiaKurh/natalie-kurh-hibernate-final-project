package com.javarush.domain;

public record Page(int offset, int limit) {

    public Page {
        if (offset < 0) {
            throw new IllegalArgumentException("Offset can't be negative!");
        }
        if (limit <= 0) {
            throw new IllegalArgumentException("Limit can't be negative!");
        }
    }
}
