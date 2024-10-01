package com.example.cinema.service.cache;

import com.example.cinema.model.MovieSession;
import com.example.cinema.model.Seat;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Component
public class ReservationCacheManager {
    private final RedisTemplate<String, Set<String>> template;

    public ReservationCacheManager(RedisTemplate<String, Set<String>> template) {
        this.template = template;
    }

    public void updateReservedSeatsCache(MovieSession movieSession, Set<String> notReservedSeatsIds) {
        template.opsForValue().set("sessionID:" + movieSession.getId(), notReservedSeatsIds);
    }

    public void removeRequestedSeatsFromNotReservedSeatsCache(MovieSession movieSession, Set<String> requestedSeatsIds) {
        Set<String> notReservedSeatsIds = getNotReservedSeatsIds(movieSession);
        notReservedSeatsIds.removeAll(requestedSeatsIds);
        updateReservedSeatsCache(movieSession, notReservedSeatsIds);
    }

    private Set<String> getNotReservedSeatsIds(MovieSession movieSession) {
        return movieSession.getSeats().stream()
                .filter(Predicate.not(Seat::isReserved))
                .map(seat -> seat.getId().toString())
                .collect(Collectors.toSet());
    }
}