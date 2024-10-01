package com.example.cinema.service;

import com.example.cinema.cache.LockManager;
import com.example.cinema.exception.reservation.*;
import com.example.cinema.model.MovieSession;
import com.example.cinema.repository.MovieSessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
public class ReservationService {
    private final MovieSessionRepository movieSessionRepository;
    private final LockManager lockManager;
    private final ReservationProcessor reservationProcessor;

    @Autowired
    public ReservationService(MovieSessionRepository movieSessionRepository,
                              LockManager lockManager,
                              ReservationProcessor reservationProcessor) {
        this.movieSessionRepository = movieSessionRepository;
        this.lockManager = lockManager;
        this.reservationProcessor = reservationProcessor;
    }

    @Transactional
    public void buyTicket(String sessionId, Set<String> requestedSeatsIds) throws TicketReservationException {
        MovieSession movieSession = movieSessionRepository.findByIdWithSeats(Long.parseLong(sessionId))
                .orElseThrow(() -> new TicketReservationException("Movie session does not exist"));

        String lockKey = "reservation:" + movieSession.getId();
        try {
            lockManager.executeWithLock(lockKey, () -> reservationProcessor.processReservation(movieSession, requestedSeatsIds));
        } catch (TicketReservationException e) {
            throw e;
        } catch (Exception e) {
            throw new TicketReservationException("Unexpected Error Occurred when Reserving Seats");
        }
    }
}
