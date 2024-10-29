package com.example.cinema.rabbitmq;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
public class RabbitMqConfig {
    public static final String RESERVATION_QUEUE = "reservation_queue";
    public static final String PAYMENT_QUEUE = "payment_queue";
    public static final String RESERVATION_EXCHANGE = "reservation_exchange";
    public static final String PAYMENT_EXCHANGE = "payment_exchange";
    public static final String RESERVATION_ROUTING_KEY = "reservation_done";
    public static final String PAYMENT_ROUTING_KEY = "payment_done";

    public Queue createQueue(String queueName) {
        return new Queue(queueName, true);
    }

    public DirectExchange createExchange(String exchangeName) {
        return new DirectExchange(exchangeName, false, false);
    }

    public Binding createBinding(Queue queue, DirectExchange exchange, String routingKey) {
        return BindingBuilder.bind(queue).to(exchange).with(routingKey);
    }

    @Bean
    public Queue reservationQueue() {
        return createQueue(RESERVATION_QUEUE);
    }

    @Bean
    public DirectExchange reservationExchange() {
        return createExchange("reservation_exchange");
    }

    @Bean
    public Binding reservationBinding(Queue reservationQueue, DirectExchange reservationExchange) {
        return createBinding(reservationQueue, reservationExchange, "reservation_done");
    }

    @Bean
    public Queue paymentQueue() {
        return createQueue(PAYMENT_QUEUE);
    }

    @Bean
    public DirectExchange paymentExchange() {
        return createExchange("payment_exchange");
    }

    @Bean
    public Binding paymentBinding(Queue paymentQueue, DirectExchange paymentExchange) {
        return createBinding(paymentQueue, paymentExchange, "payment_done");
    }

    @Bean
    public RabbitMqMessageHandler rabbitMqMessageHandler() {
        return new RabbitMqMessageHandler(rabbitTemplate(connectionFactory()));
    }

    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory("localhost");
        connectionFactory.setPort(5672);
        return connectionFactory;
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        return new RabbitTemplate(connectionFactory);
    }
}
