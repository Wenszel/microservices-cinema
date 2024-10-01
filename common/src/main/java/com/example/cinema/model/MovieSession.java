package com.example.cinema.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Getter
@NoArgsConstructor
@Entity
public class MovieSession {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    private Movie movie;
    @ManyToOne(fetch = FetchType.LAZY)
    private Room room;
    private LocalDateTime startDateTime = LocalDateTime.of(2025, 1, 1, 0, 0);
    @OneToMany(mappedBy = "movieSession")
    private Set<Seat> seats;

    public MovieSession(Movie movie, Room room,
                        LocalDateTime startDateTime, Collection<Seat> seats) {
        this.movie = movie;
        this.room = room;
        this.startDateTime = startDateTime;
        this.seats = new HashSet<>(seats);
        this.room.getMovieSessions().add(this);
        this.movie.getMovieSessions().add(this);
        this.seats.forEach(seat -> seat.setMovieSession(this));
    }
}
