package com.example.cinema.rabbitmq;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqQueues {
    public static final String RESERVATION_EXCHANGE = "reservation_exchange";
    public static final String RESERVATION_ROUTING_KEY = "reservation_done";
    public static final String RESERVATION_QUEUE = "reservation_queue";

    public static final String PAYMENT_EXCHANGE = "payment_exchange";
    public static final String PAYMENT_ROUTING_KEY = "payment_done";
    public static final String PAYMENT_QUEUE = "payment_queue";

    @Bean
    public Queue reservationQueue() {
        return new Queue(RESERVATION_QUEUE, true);
    }

    @Bean
    public Queue paymentQueue() {
        return new Queue(PAYMENT_QUEUE, true);
    }

    @Bean
    public DirectExchange reservationExchange() {
        return new DirectExchange(RESERVATION_EXCHANGE, false, false);
    }

    @Bean
    public Binding reservationBinding(Queue reservationQueue, DirectExchange reservationExchange) {
        return BindingBuilder.bind(reservationQueue).to(reservationExchange).with(RESERVATION_ROUTING_KEY);
    }

    @Bean
    public RabbitTemplate template() {
        return new RabbitTemplate();
    }

    @Bean
    public RabbitMqMessageHandler rabbitMqMessageHandler() {
        return new RabbitMqMessageHandler(template());
    }
}
