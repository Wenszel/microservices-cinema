package com.example.cinema.cache;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LockManager {

    private final RedissonClient redissonClient;

    @Autowired
    public LockManager(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    public <T> void executeWithLock(String lockKey, LockAction<T> action) throws Exception {
        RLock lock = redissonClient.getLock(lockKey);
        boolean lockAcquired = false;
        try {
            lockAcquired = lock.tryLock();
            if (!lockAcquired) {
                throw new RuntimeException("Could not acquire lock for key: " + lockKey);
            }
            action.execute();
        } finally {
            if (lockAcquired) {
                lock.unlock();
            }
        }
    }
}

