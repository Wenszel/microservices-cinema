package com.example.cinema.controller;

import com.example.cinema.dto.request.ReservationRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ReservationControllerTests {
    @InjectMocks private ReservationController reservationController;
    @Mock private RabbitTemplate rabbitTemplate;

    @Autowired private MockMvc mockMvc;

    private final Message OK_MESSAGE = new Message("OK".getBytes());
    private final Message ERROR_MESSAGE = new Message("ERROR error message".getBytes());
    private final ReservationRequest VALID_RESERVATION_REQUEST = new ReservationRequest("1", List.of("A1"));
    private final ReservationRequest NO_SEATS_LIST_RESERVATION_REQUEST = new ReservationRequest("1", null);
    private final ReservationRequest EMPTY_SEATS_LIST_RESERVATION_REQUEST = new ReservationRequest("1", new ArrayList<>());
    private final ReservationRequest NO_SESSION_ID_RESERVATION_REQUEST = new ReservationRequest(null, List.of("A1"));

    @Test
    public void givenReservationWithoutSeatsList_whenReserve_thenStatus400() throws Exception {
        String requestJson = new ObjectMapper().writeValueAsString(NO_SEATS_LIST_RESERVATION_REQUEST);

        mockMvc.perform(post("/reservation")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isBadRequest());

        verify(rabbitTemplate, times(0)).sendAndReceive(anyString(), anyString(), any(Message.class));
    }

    @Test
    public void givenReservationWithNoSeats_whenReserve_thenStatus400() throws Exception {
        String requestJson = new ObjectMapper().writeValueAsString(EMPTY_SEATS_LIST_RESERVATION_REQUEST);

        mockMvc.perform(post("/reservation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest());

        verify(rabbitTemplate, times(0)).sendAndReceive(anyString(), anyString(), any(Message.class));
    }

    @Test
    public void givenReservationWithoutSession_whenReserve_thenStatus400() throws Exception {
        String requestJson = new ObjectMapper().writeValueAsString(NO_SESSION_ID_RESERVATION_REQUEST);

        mockMvc.perform(post("/reservation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest());

        verify(rabbitTemplate, times(0)).sendAndReceive(anyString(), anyString(), any(Message.class));
    }

    @Test
    public void givenCorrectReservation_whenReserve_thenStatus200() {
        when(rabbitTemplate.sendAndReceive(
                        anyString(), anyString(), any(Message.class)))
                .thenReturn(OK_MESSAGE);

        ResponseEntity<String> response = reservationController.reserve(VALID_RESERVATION_REQUEST);

        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
        assertEquals("OK", response.getBody());
    }

    @Test
    public void givenValidReservationWithReservationFailure_whenReserve_thenStatus400() {
        when(rabbitTemplate.sendAndReceive(
                        anyString(), anyString(), any(Message.class)))
                .thenReturn(ERROR_MESSAGE);

        ResponseEntity<String> response = reservationController.reserve(VALID_RESERVATION_REQUEST);

        assertEquals(HttpStatusCode.valueOf(400), response.getStatusCode());
        assertEquals("error message", response.getBody());
    }
}
