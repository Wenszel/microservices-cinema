package com.example.cinema.exception.reservation;

public class SeatsNotFoundException extends TicketReservationException {
    public SeatsNotFoundException() {
        super("Some seats are not found");
    }
}
