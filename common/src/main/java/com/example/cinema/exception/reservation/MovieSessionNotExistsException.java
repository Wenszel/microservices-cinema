package com.example.cinema.exception.reservation;

public class MovieSessionNotExistsException extends TicketReservationException {
    public MovieSessionNotExistsException() {
        super("Movie session does not exist");
    }
}
