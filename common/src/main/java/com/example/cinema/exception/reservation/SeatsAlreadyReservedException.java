package com.example.cinema.exception.reservation;

public class SeatsAlreadyReservedException extends TicketReservationException {
    public SeatsAlreadyReservedException() {
        super("Some seats are already reserved");
    }
}
