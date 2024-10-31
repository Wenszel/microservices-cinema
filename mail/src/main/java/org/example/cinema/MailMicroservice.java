package org.example.cinema;


import jakarta.annotation.PostConstruct;
import org.example.cinema.service.SendMailService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Collections;

@SpringBootApplication
public class MailMicroservice {
    public SendMailService sendMailService;

    public MailMicroservice(SendMailService sendMailService) {
        this.sendMailService = sendMailService;
    }

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(MailMicroservice.class);
        app.setDefaultProperties(Collections.singletonMap("server.port", "8083"));
        app.run(args);
    }
    @PostConstruct
    public void init() {
        sendMailService.sendEmail("foo@bar.com", "Test", "Test");
    }
}