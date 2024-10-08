package com.example.cinema.rabbitmq;

public record RabbitMqClientData(String replyTo, String correlationId) {}
