package org.example.paymentclient.payu;

import com.example.cinema.exception.payment.AccessTokenRetrievalException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.token.AccessToken;
import org.springframework.stereotype.Component;

@Component
public class PayUResponseParser {
    private final ObjectMapper objectMapper = new ObjectMapper();
    public String parseRedirectionUri(String response) {
        try {
            return objectMapper.readTree(response).get("redirectUri").asText();
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse response", e);
        }
    }

    public AccessToken parseAccessToken(String response) throws AccessTokenRetrievalException {
        try {
            String accessToken = objectMapper.readTree(response).get("access_token").asText();
            int expiresIn = objectMapper.readTree(response).get("expires_in").asInt();
            return new AccessToken(accessToken, expiresIn);
        } catch (Exception e) {
            throw new AccessTokenRetrievalException();
        }
    }
}
