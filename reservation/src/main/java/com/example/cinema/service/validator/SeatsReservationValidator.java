package com.example.cinema.service.validator;

import com.example.cinema.exception.reservation.InvalidSeatsException;
import com.example.cinema.exception.reservation.SeatsAlreadyReservedException;
import com.example.cinema.exception.reservation.SeatsNotFoundException;
import com.example.cinema.exception.reservation.TicketReservationException;
import com.example.cinema.model.MovieSession;
import com.example.cinema.model.Seat;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class SeatsReservationValidator {
    public void validate(MovieSession movieSession, Set<Seat> requestedSeats) throws TicketReservationException  {
       checkIfAllSeatsFromCorrectMovieSession(movieSession, requestedSeats);
       checkIfAllRequestedSeatsAreFree(movieSession, requestedSeats);
    }

    public void checkIfAllSeatsAreInDatabase(Set<String> requestedSeatsIds, Set<Seat> requestedSeats) throws SeatsNotFoundException {
        if (requestedSeats.size() != requestedSeatsIds.size())
            throw new SeatsNotFoundException();
    }

    private void checkIfAllRequestedSeatsAreFree(MovieSession movieSession, Set<Seat> requestedSeats) throws SeatsAlreadyReservedException {
        Set<Seat> notReservedSeats = getNotReservedSeats(movieSession);
        if (!notReservedSeats.containsAll(requestedSeats)) {
            throw new SeatsAlreadyReservedException();
        }
    }

    private void checkIfAllSeatsFromCorrectMovieSession(MovieSession movieSession, Set<Seat> requestedSeats) throws InvalidSeatsException {
        if (!movieSession.getSeats().containsAll(requestedSeats)) throw new InvalidSeatsException();
    }

    private Set<Seat> getNotReservedSeats(MovieSession movieSession) {
        return movieSession.getSeats().stream()
                .filter(seat -> !seat.isReserved())
                .collect(Collectors.toSet());
    }
}
