package org.example.paymentclient.payu;

import com.example.cinema.dto.request.PaymentRequest;
import com.example.cinema.exception.payment.AccessTokenRetrievalException;
import jakarta.annotation.PostConstruct;
import org.example.token.AccessToken;
import org.example.token.TokenCache;
import org.example.paymentclient.PaymentApiClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class PayUClient implements PaymentApiClient {
    private final TokenCache tokenCache;
    public static String ORDER_URL = "https://secure.snd.payu.com/api/v2_1/orders";
    public static String TOKEN_URL = "https://secure.snd.payu.com/pl/standard/user/oauth/authorize";
    private final String clientId;
    private final String clientSecret;
    private final PayUResponseParser payUResponseParser;

    public PayUClient(TokenCache tokenCache,
                        @Value("${payment.payu.clientid}") String clientId,
                        @Value("${payment.payu.secret}") String clientSecret,
                      PayUResponseParser payUResponseParser) {
        this.tokenCache = tokenCache;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.payUResponseParser = payUResponseParser;
    }

//    @PostConstruct
//    public void init() throws AccessTokenRetrievalException {
//        AccessToken accessToken = getAccessToken();
//        tokenCache.addToken(accessToken.token(), accessToken.expiresIn());
//    }

    @Override
    public String pay(PaymentRequest paymentRequest) {
        String response = WebClient.create()
                        .post()
                        .uri(ORDER_URL)
                        .header("Authorization", "Bearer " + tokenCache.getToken())
                        .bodyValue(paymentRequest)
                        .retrieve()
                        .bodyToMono(String.class)
                        .block();
        if (response == null) {
            throw new RuntimeException("Failed to create order");
        }
        return payUResponseParser.parseRedirectionUri(response);
    }

    @Override
    public AccessToken getAccessToken() throws AccessTokenRetrievalException {
        String response = fetchAccessToken();
        return payUResponseParser.parseAccessToken(response);
    }

    private String fetchAccessToken() {
        return WebClient.create().post()
                 .uri(TOKEN_URL)
                 .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                 .bodyValue(getAccessTokenBody())
                 .retrieve()
                 .bodyToMono(String.class)
                 .block();
    }

    private MultiValueMap<String, String> getAccessTokenBody() {
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("grant_type", "client_credentials");
        formData.add("client_id", clientId);
        formData.add("client_secret", clientSecret);
        return formData;
    }
}
