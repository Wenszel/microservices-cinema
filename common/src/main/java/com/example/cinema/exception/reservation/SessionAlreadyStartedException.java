package com.example.cinema.exception.reservation;

public class SessionAlreadyStartedException extends TicketReservationException {
    public SessionAlreadyStartedException() {
        super("Session is already finished");
    }
}
