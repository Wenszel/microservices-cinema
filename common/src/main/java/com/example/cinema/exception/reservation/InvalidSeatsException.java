package com.example.cinema.exception.reservation;

public class InvalidSeatsException extends TicketReservationException {
    public InvalidSeatsException() {
        super("Some seats do not belong to the session");
    }
}
