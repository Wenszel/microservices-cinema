package com.example.cinema.service.validator;

import com.example.cinema.exception.reservation.SessionAlreadyStartedException;
import com.example.cinema.model.MovieSession;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MovieSessionReservationValidatorTests {
    @Test
    public void shouldThrowExceptionWhenMovieSessionIsStarted() {
        // given
        MovieSession movieSession = mock(MovieSession.class);
        when(movieSession.getStartDateTime()).thenReturn(LocalDateTime.now().minusHours(1));
        // when
        MovieSessionReservationValidator movieSessionReservationValidator = new MovieSessionReservationValidator();
        // then
        assertThrows(SessionAlreadyStartedException.class, () -> movieSessionReservationValidator.validate(movieSession));
    }

    @Test
    public void shouldNotThrowExceptionWhenMovieSessionIsNotStarted() throws SessionAlreadyStartedException {
        // given
        MovieSession movieSession = mock(MovieSession.class);
        when(movieSession.getStartDateTime()).thenReturn(LocalDateTime.now().plusHours(1));
        // when
        MovieSessionReservationValidator movieSessionReservationValidator = new MovieSessionReservationValidator();
        // then
        movieSessionReservationValidator.validate(movieSession);
    }
}
