package com.example.cinema.service.cache;

import com.example.cinema.model.MovieSession;
import com.example.cinema.model.Seat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservationCacheManagerTests {
    @Mock
    private RedisTemplate<String, Set<String>> redisTemplate;
    @Mock
    private ValueOperations<String, Set<String>> valueOperations;
    @Captor
    private ArgumentCaptor<Set<String>> captor;
    @InjectMocks
    private ReservationCacheManager reservationCacheManager;
    private MovieSession movieSession;

    @BeforeEach
    public void setUp() {
        movieSession = mock(MovieSession.class);
        when(movieSession.getId()).thenReturn(1L);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    @Test
    public void updateReservedSeatsCache_ShouldSetCacheValue() {
        Set<String> notReservedSeatsIds = Set.of("1", "3");

        reservationCacheManager.updateReservedSeatsCache(movieSession, notReservedSeatsIds);

        verify(redisTemplate.opsForValue()).set("sessionID:1", notReservedSeatsIds);
    }

    @Test
    public void removeRequestedSeatsFromNotReservedSeatsCache_ShouldRemoveRequestedSeats() {
        Set<String> requestedSeatsIds = Set.of("1");
        Seat seat = mock(Seat.class);
        when(seat.getId()).thenReturn(1L);
        Seat notRequestedSeat = mock(Seat.class);
        when(notRequestedSeat.getId()).thenReturn(3L);
        when(movieSession.getSeats()).thenReturn(Set.of(seat, notRequestedSeat));
        reservationCacheManager.removeRequestedSeatsFromNotReservedSeatsCache(movieSession, requestedSeatsIds);

        verify(redisTemplate.opsForValue()).set(eq("sessionID:1"), captor.capture());

        Set<String> updatedSet = captor.getValue();
        assertEquals(Set.of("3"), updatedSet);
    }
}
