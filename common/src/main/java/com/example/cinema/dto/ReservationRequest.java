package com.example.cinema.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class ReservationRequest {
    @NotNull
    private String sessionId;
    @NotNull
    @Size(min = 1)
    private List<String> seats;
}