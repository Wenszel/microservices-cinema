package com.example.cinema.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@AllArgsConstructor
@Data
public class ReservationRequest {
    @NotNull
    private String sessionId;
    @NotNull
    @Size(min = 1)
    private List<String> seats;
}