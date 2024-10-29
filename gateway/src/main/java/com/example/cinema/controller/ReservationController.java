package com.example.cinema.controller;

import com.example.cinema.dto.request.ReservationRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;

import static com.example.cinema.rabbitmq.RabbitMqConfig.RESERVATION_EXCHANGE;
import static com.example.cinema.rabbitmq.RabbitMqConfig.RESERVATION_ROUTING_KEY;

@RestController
public class ReservationController {
    private final RabbitTemplate rabbitTemplate;

    @Autowired
    public ReservationController(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @PostMapping("/reservation")
    public ResponseEntity<String> reserve(@Valid @RequestBody ReservationRequest request) {
        try {
            Message message = getMessageFromRequest(request);
            Message response = rabbitTemplate.sendAndReceive(RESERVATION_EXCHANGE, RESERVATION_ROUTING_KEY, message);

            if (response == null) {
                return ResponseEntity.badRequest().body("Reservation server failed");
            }

            String responseString = new String(response.getBody(), StandardCharsets.UTF_8);

            if (responseString.startsWith("ERROR")) {
                return handleErrorResponse(responseString);
            }

            return ResponseEntity.ok(responseString);
        } catch (JsonProcessingException e) {
            return ResponseEntity.badRequest().body("Invalid request");
        }
    }

    private Message getMessageFromRequest(ReservationRequest request) throws JsonProcessingException {
        String json = new ObjectMapper().writeValueAsString(request);
        return new Message(json.getBytes(StandardCharsets.UTF_8));
    }

    private ResponseEntity<String> handleErrorResponse(String responseString) {
        String errorMessage = responseString.length() > 6 ? responseString.substring(6) : "Unknown error";
        return ResponseEntity.badRequest().body(errorMessage);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }
}
