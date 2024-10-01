package com.example.cinema.service;

import com.example.cinema.cache.LockAction;
import com.example.cinema.cache.LockManager;
import com.example.cinema.exception.reservation.InvalidSeatsException;
import com.example.cinema.exception.reservation.SeatsNotFoundException;
import com.example.cinema.exception.reservation.SessionAlreadyStartedException;
import com.example.cinema.exception.reservation.TicketReservationException;
import com.example.cinema.model.MovieSession;
import com.example.cinema.model.Seat;
import com.example.cinema.repository.MovieSessionRepository;
import com.example.cinema.repository.SeatsRepository;
import com.example.cinema.service.validator.SeatsReservationValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReservationServiceTests {
    @Mock private MovieSessionRepository movieSessionRepository;
    @Mock private SeatsRepository seatsRepository;
    @Mock private RedisTemplate<String, Set<String>> template;
    @Mock private ValueOperations<String, Set<String>> valueOperations;
    @Mock private LockManager lockManager;
    @Mock private SeatsReservationValidator seatsReservationValidator;
    @Mock private ReservationProcessor reservationProcessor;
    @InjectMocks private ReservationService reservationService;
    private final String sessionId = "1";


//    @Test
//    public void shouldReserveSeatsSuccessfully() throws Exception {
//        mockCache();
//        mockLockManager();
//        Set<String> requestedSeatsIds = new HashSet<>(Arrays.asList("1", "2"));
//
//        Set<Seat> seats = mockSeats();
//        reservationService.buyTicket(sessionId, requestedSeatsIds);
//
//        verifySeatReservations(seats);
//        verify(lockManager).executeWithLock(eq("reservation:1"), any());
//        verify(valueOperations).set(eq("sessionID:1"), anySet());
//    }
//
//    @Test
//    public void shouldThrowExceptionWhenMovieSessionDoesNotExist() {
//        Set<String> requestedSeatsIds = new HashSet<>(Arrays.asList("1", "2"));
//
//        when(movieSessionRepository.findByIdWithSeats(Long.parseLong(sessionId)))
//                .thenReturn(Optional.empty());
//
//        Exception exception = assertThrows(TicketReservationException.class, () -> reservationService.buyTicket(sessionId, requestedSeatsIds));
//        String expectedMessage = "Movie session does not exist";
//        assertEqualExceptionMessage(expectedMessage, exception);
//    }
//    private Seat mockSeat(long id) {
//        Seat seat = mock(Seat.class);
//        when(seat.getId()).thenReturn(id);
//        return seat;
//    }
//
//    private MovieSession mockValidMovieSession() {
//        MovieSession movieSession = mock(MovieSession.class);
//        when(movieSession.getId()).thenReturn(1L);
//        when(movieSession.getStartDateTime()).thenReturn(LocalDateTime.now().plusMinutes(10));
//        return movieSession;
//    }
//
//    private MovieSession mockStartedMovieSession() {
//        MovieSession movieSession = mock(MovieSession.class);
//        when(movieSession.getId()).thenReturn(1L);
//        when(movieSession.getStartDateTime()).thenReturn(LocalDateTime.now().minusMinutes(100));
//        return movieSession;
//    }
//
//    @Test
//    public void shouldThrowExceptionWhenMovieSessionStarted() throws Exception {
//        mockLockManager();
//        Set<String> requestedSeatsIds = new HashSet<>(Arrays.asList("1", "2"));
//
//        MovieSession movieSession = mockStartedMovieSession();
//        when(movieSessionRepository.findByIdWithSeats(Long.parseLong(sessionId)))
//                .thenReturn(Optional.of(movieSession));
//
//        assertThrows(SessionAlreadyStartedException.class, () -> reservationService.buyTicket(sessionId, requestedSeatsIds));
//    }
//
//    @Test
//    public void shouldThrowExceptionWhenAnySeatDoesNotExist() {
//        Set<String> requestedSeatsIds = new HashSet<>(Arrays.asList("1", "2", "3"));
//
//        mockSeats();
//
//        assertThrows(SeatsNotFoundException.class, () ->
//                reservationService.buyTicket(sessionId, requestedSeatsIds));
//    }
//
//    @Test
//    public void shouldThrowExceptionWhenSomeSeatDoesNotBelongToMovieSession() {
//        Set<String> requestedSeatsIds = new HashSet<>(List.of("3"));
//
//        Set<Seat> seats = new HashSet<>();
//        seats.add(mock(Seat.class));
//        seats.add(mock(Seat.class));
//        MovieSession movieSession = mock(MovieSession.class);
//        when(movieSession.getId()).thenReturn(1L);
//        when(movieSession.getSeats()).thenReturn(seats);
//        when(movieSession.getStartDateTime()).thenReturn(LocalDateTime.now().plusMinutes(10));
//        when(movieSessionRepository.findByIdWithSeats(Long.parseLong(sessionId)))
//                .thenReturn(Optional.of(movieSession));
//        when(seatsRepository.findAllById(anySet())).thenReturn(List.of(mock(Seat.class)));
//        assertThrows(InvalidSeatsException.class, () ->
//                reservationService.buyTicket(sessionId, requestedSeatsIds));
//    }
//
//
//    private void assertEqualExceptionMessage(String expectedMessage, Exception exception) {
//        String actualMessage = exception.getMessage();
//        assertEquals(expectedMessage, actualMessage);
//    }
//
//
//    private void verifySeatReservations(Set<Seat> seats) {
//        for (Seat seat : seats) {
//            verify(seat).reserve();
//        }
//    }
//
//    private Set<Seat> mockSeats() {
//        MovieSession movieSession = mock(MovieSession.class);
//
//        Seat seat1 = mock(Seat.class);
//        when(seat1.getId()).thenReturn(1L);
//        Seat seat2 = mock(Seat.class);
//        when(seat2.getId()).thenReturn(2L);
//        Set<Seat> seats = new HashSet<>(Arrays.asList(seat1, seat2));
//
//        when(movieSession.getId()).thenReturn(1L);
//        when(movieSession.getSeats()).thenReturn(seats);
//        when(movieSession.getStartDateTime()).thenReturn(LocalDateTime.now().plusMinutes(10));
//        when(movieSessionRepository.findByIdWithSeats(Long.parseLong(sessionId)))
//                .thenReturn(Optional.of(movieSession));
//
//        when(seatsRepository.findAllById(anySet())).thenReturn(Arrays.asList(seat1, seat2));
//        return seats;
//    }
//
//    private void mockLockManager() throws Exception {
//        doAnswer(invocation -> {
//            LockAction<Object> task = invocation.getArgument(1);
//            task.execute();
//            return null;
//        }).when(lockManager).executeWithLock(any(), any());
//    }
//
//    private void mockCache() {
//        when(template.opsForValue()).thenReturn(valueOperations);
//        doNothing().when(valueOperations).set(anyString(), anySet());
//    }
}