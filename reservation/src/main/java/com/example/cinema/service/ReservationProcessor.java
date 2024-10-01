package com.example.cinema.service;

import com.example.cinema.exception.reservation.*;
import com.example.cinema.model.MovieSession;
import com.example.cinema.model.Seat;
import com.example.cinema.repository.SeatsRepository;
import com.example.cinema.service.cache.ReservationCacheManager;
import com.example.cinema.service.validator.MovieSessionReservationValidator;
import com.example.cinema.service.validator.SeatsReservationValidator;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class ReservationProcessor {
    private final SeatsRepository seatsRepository;
    private final ReservationCacheManager reservationCacheManager;
    private final MovieSessionReservationValidator movieSessionReservationValidator;
    private final SeatsReservationValidator seatsReservationValidator;

    public ReservationProcessor(SeatsRepository seatsRepository,
                                ReservationCacheManager reservationCacheManager,
                                MovieSessionReservationValidator movieSessionReservationValidator,
                                SeatsReservationValidator seatsReservationValidator) {
        this.seatsRepository = seatsRepository;
        this.reservationCacheManager = reservationCacheManager;
        this.movieSessionReservationValidator = movieSessionReservationValidator;
        this.seatsReservationValidator = seatsReservationValidator;
    }

    public void processReservation(MovieSession movieSession, Set<String> requestedSeatsIds) throws TicketReservationException {
        Set<Seat> requestedSeats = getRequestedSeatsByIdsFromDatabase(requestedSeatsIds);

        movieSessionReservationValidator.validate(movieSession);
        seatsReservationValidator.checkIfAllSeatsAreInDatabase(requestedSeatsIds, requestedSeats);
        seatsReservationValidator.validate(movieSession, requestedSeats);

        reservationCacheManager.removeRequestedSeatsFromNotReservedSeatsCache(movieSession, requestedSeatsIds);

        markSeatsAsReserved(movieSession, requestedSeatsIds);
    }

    private Set<Seat> getRequestedSeatsByIdsFromDatabase(Set<String> requestedSeatsIds) throws SeatsNotFoundException {
        Set<Long> seatIds = mapIdsToLong(requestedSeatsIds);
        return new HashSet<>(seatsRepository.findAllById(seatIds));
    }

    private Set<Long> mapIdsToLong(Set<String> requestedSeatsIds) {
        return requestedSeatsIds.stream()
                .map(Long::parseLong)
                .collect(Collectors.toSet());
    }

    private void markSeatsAsReserved(MovieSession movieSession, Set<String> requestedSeatsIds) {
        requestedSeatsIds.stream()
                .map(Long::parseLong)
                .map(id -> findSeatInMovieSessionById(movieSession, id))
                .flatMap(Optional::stream)
                .forEach(Seat::reserve);
    }

    private Optional<Seat> findSeatInMovieSessionById(MovieSession movieSession, Long seatId) {
        return movieSession.getSeats().stream()
                .filter(seat -> seat.getId().equals(seatId))
                .findFirst();
    }
}
