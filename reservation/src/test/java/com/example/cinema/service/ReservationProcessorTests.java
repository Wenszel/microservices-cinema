package com.example.cinema.service;

import com.example.cinema.exception.reservation.*;
import com.example.cinema.model.MovieSession;
import com.example.cinema.model.Seat;
import com.example.cinema.repository.SeatsRepository;
import com.example.cinema.service.cache.ReservationCacheManager;
import com.example.cinema.service.validator.MovieSessionReservationValidator;
import com.example.cinema.service.validator.SeatsReservationValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReservationProcessorTests {
    @Mock
    private SeatsRepository seatsRepository;
    @Mock
    private ReservationCacheManager reservationCacheManager;
    @Mock
    private MovieSessionReservationValidator movieSessionReservationValidator;
    @Mock
    private SeatsReservationValidator seatsReservationValidator;
    @InjectMocks
    private ReservationProcessor reservationProcessor;

    @Test
    public void shouldCorrectlyProcessReservationWhenNoValidationErrors() throws TicketReservationException {
        MovieSession movieSession = mock(MovieSession.class);
        List<Seat> requestedSeats = new ArrayList<>();
        Set<String> requestedSeatsIds = new HashSet<>();
        requestedSeatsIds.add("1");
        requestedSeatsIds.add("2");

        for (String id : requestedSeatsIds) {
            Seat seat = mock(Seat.class);
            when(seat.getId()).thenReturn(Long.parseLong(id));
            requestedSeats.add(seat);
        }

        when(movieSession.getSeats()).thenReturn(new HashSet<>(requestedSeats));
        when(seatsRepository.findAllById(any())).thenReturn(requestedSeats);

        doNothing().when(movieSessionReservationValidator).validate(any());
        doNothing().when(seatsReservationValidator).checkIfAllSeatsAreInDatabase(any(), any());
        doNothing().when(seatsReservationValidator).validate(any(), any());

        reservationProcessor.processReservation(movieSession, requestedSeatsIds);

        verify(reservationCacheManager, times(1)).removeRequestedSeatsFromNotReservedSeatsCache(any(), any());
        for (Seat seat : requestedSeats) {
            verify(seat, times(1)).reserve();
        }
    }

    @Test
    public void shouldThrowErrorWhenMovieSessionReservationValidatorFoundsError() throws TicketReservationException{
        MovieSession movieSession = mock(MovieSession.class);
        Set<String> requestedSeatsIds = new HashSet<>();

        doThrow(new SessionAlreadyStartedException()).when(movieSessionReservationValidator).validate(movieSession);

        try {
            reservationProcessor.processReservation(movieSession, requestedSeatsIds);
        } catch (TicketReservationException e) {
            verifyCacheManagerNotInvoked();
        }
    }

    @Test
    public void shouldThrowErrorWhenSeatsReservationValidatorFoundsDatabaseError() throws TicketReservationException {
        MovieSession movieSession = mock(MovieSession.class);
        Set<String> requestedSeatsIds = new HashSet<>();

        doNothing().when(movieSessionReservationValidator).validate(any());
        doThrow(new SeatsNotFoundException()).when(seatsReservationValidator).checkIfAllSeatsAreInDatabase(any(), any());

        try {
            reservationProcessor.processReservation(movieSession, requestedSeatsIds);
        } catch (TicketReservationException e) {
            verifyCacheManagerNotInvoked();
        }
    }

    @Test
    public void shouldThrowErrorWhenSeatsReservationValidatorFoundsError() throws TicketReservationException {
        MovieSession movieSession = mock(MovieSession.class);
        Set<String> requestedSeatsIds = new HashSet<>();

        doNothing().when(movieSessionReservationValidator).validate(any());
        doNothing().when(seatsReservationValidator).checkIfAllSeatsAreInDatabase(any(), any());
        doThrow(new TicketReservationException("error")).when(seatsReservationValidator).validate(any(), any());

        try {
            reservationProcessor.processReservation(movieSession, requestedSeatsIds);
        } catch (TicketReservationException e) {
            verifyCacheManagerNotInvoked();
        }
    }

    private void verifyCacheManagerNotInvoked() {
        verify(reservationCacheManager, never()).removeRequestedSeatsFromNotReservedSeatsCache(any(), any());
    }
}
