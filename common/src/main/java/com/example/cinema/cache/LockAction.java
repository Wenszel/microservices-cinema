package com.example.cinema.cache;

@FunctionalInterface
public interface LockAction<T> {
    void execute() throws Exception;
}
