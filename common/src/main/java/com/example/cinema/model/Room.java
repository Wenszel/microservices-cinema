package com.example.cinema.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;
@Entity
@Data
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private int capacity;
    private int rows;
    private int columns;
    @OneToMany(mappedBy = "room")
    private Set<MovieSession> movieSessions = new HashSet<>();
    private record Coords(int row, int column) {
    }
}
