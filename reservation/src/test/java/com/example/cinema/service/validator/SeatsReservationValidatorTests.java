package com.example.cinema.service.validator;

import com.example.cinema.exception.reservation.InvalidSeatsException;
import com.example.cinema.exception.reservation.SeatsAlreadyReservedException;
import com.example.cinema.exception.reservation.SeatsNotFoundException;
import com.example.cinema.model.MovieSession;
import com.example.cinema.model.Seat;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SeatsReservationValidatorTests {
    @Test
    public void shouldThrowExceptionWhenSeatsNotFromGivenMovieSession() {
        MovieSession movieSession = mock(MovieSession.class);
        when(movieSession.getSeats()).thenReturn(new HashSet<>());
        Set<Seat> requestedSeats = Set.of(mock(Seat.class));

        SeatsReservationValidator seatsReservationValidator = new SeatsReservationValidator();
        Assertions.assertThrows(InvalidSeatsException.class, () -> seatsReservationValidator.validate(movieSession, requestedSeats));
    }

    @Test
    public void shouldThrowExceptionWhenSeatsAlreadyReserved() {
        MovieSession movieSession = mock(MovieSession.class);
        Seat reservedSeat = mock(Seat.class);
        when(reservedSeat.isReserved()).thenReturn(true);
        Set<Seat> requestedSeats = Set.of(reservedSeat);
        when(movieSession.getSeats()).thenReturn(requestedSeats);

        SeatsReservationValidator seatsReservationValidator = new SeatsReservationValidator();
        Assertions.assertThrows(SeatsAlreadyReservedException.class, () -> seatsReservationValidator.validate(movieSession, requestedSeats));
    }

    @Test
    public void shouldCorrectlyValidateSeats() {
        MovieSession movieSession = mock(MovieSession.class);
        Seat seat = mock(Seat.class);
        when(seat.isReserved()).thenReturn(false);
        Set<Seat> requestedSeats = Set.of(seat);
        when(movieSession.getSeats()).thenReturn(requestedSeats);

        SeatsReservationValidator seatsReservationValidator = new SeatsReservationValidator();
        Assertions.assertDoesNotThrow(() -> seatsReservationValidator.validate(movieSession, requestedSeats));
    }

    @Test
    public void shouldThrowExceptionWhenRequestedSeatNotInDatabase() {
        Set<String> requestedSeatsIds = Set.of("1");
        Seat requestedSeat = mock(Seat.class);
        when(requestedSeat.getId()).thenReturn(2L);

        SeatsReservationValidator seatsReservationValidator = new SeatsReservationValidator();
        Assertions.assertThrows(SeatsNotFoundException.class, () -> seatsReservationValidator.checkIfAllSeatsAreInDatabase(requestedSeatsIds, new HashSet<>()));
    }
}
