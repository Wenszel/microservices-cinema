package com.example.paymentclient.payu;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class PayUHttpClient {
    public String post(String url, HttpHeaders headers, Object body) {

        return WebClient.create().post()
                .uri(url)
                .headers(h -> h.addAll(headers))
                .bodyValue(body)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

    public String postForm(String url, MultiValueMap<String, String> formData) {
        return WebClient.create().post()
                .uri(url)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .bodyValue(formData)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }
}
