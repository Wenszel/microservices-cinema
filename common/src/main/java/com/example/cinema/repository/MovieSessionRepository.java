package com.example.cinema.repository;

import com.example.cinema.model.MovieSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MovieSessionRepository extends JpaRepository<MovieSession, Long> {
    @Query("SELECT ms FROM MovieSession ms LEFT JOIN FETCH ms.seats WHERE ms.id = :id")
    public Optional<MovieSession> findByIdWithSeats(Long id);
}
