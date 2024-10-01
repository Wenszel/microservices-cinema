package com.example.cinema.service.validator;

import com.example.cinema.exception.reservation.SessionAlreadyStartedException;
import com.example.cinema.model.MovieSession;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class MovieSessionReservationValidator {
    public void validate(MovieSession movieSession) throws SessionAlreadyStartedException {
        checkIfMovieSessionAlreadyStarted(movieSession);
    }

    private void checkIfMovieSessionAlreadyStarted(MovieSession movieSession) throws SessionAlreadyStartedException {
        if (isMovieSessionStarted(movieSession))
            throw new SessionAlreadyStartedException();
    }

    private boolean isMovieSessionStarted(MovieSession movieSession) {
        return movieSession.getStartDateTime().isBefore(LocalDateTime.now());
    }
}
