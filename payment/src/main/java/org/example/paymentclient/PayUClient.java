package org.example.paymentclient;

import com.example.cinema.dto.PaymentRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.TokenCache;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class PayUClient implements PaymentApiClient {
    private final TokenCache tokenCache;
    public static String ORDER_URL = "https://secure.snd.payu.com/api/v2_1/orders";
    public static String TOKEN_URL = "https://secure.snd.payu.com/pl/standard/user/oauth/authorize";
    @Value("${payment.payu.clientid}")
    private final String clientId;
    @Value("${payment.payu.secret}")
    private final String clientSecret;

    public PayUClient(TokenCache tokenCache,
                      String clientId,
                      String clientSecret) {
        this.tokenCache = tokenCache;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
    }

    @Override
    public void pay(PaymentRequest paymentRequest) {
        String response = WebClient.create()
                        .post()
                        .uri(ORDER_URL)
                        .header("Authorization", "Bearer " + getAccessToken())
                        .bodyValue(paymentRequest)
                        .retrieve()
                        .bodyToMono(String.class)
                        .block();
        System.out.println("Paying for session: " + paymentRequest.getSessionId());
    }

    @Override
    public String getAccessToken() {
       String response = WebClient.create().post()
                .uri(TOKEN_URL)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .bodyValue(getAccessTokenBody())
                .retrieve()
                .bodyToMono(String.class)
                .block();

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String accessToken = objectMapper.readTree(response).get("access_token").asText();
            int expiresIn = objectMapper.readTree(response).get("expires_in").asInt();
            tokenCache.addToken(accessToken, expiresIn);
            return accessToken;
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to parse access token");
        }

    }

    private MultiValueMap<String, String> getAccessTokenBody() {
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("grant_type", "client_credentials");
        formData.add("client_id", clientId);
        formData.add("client_secret", clientSecret);
        return formData;
    }
}
