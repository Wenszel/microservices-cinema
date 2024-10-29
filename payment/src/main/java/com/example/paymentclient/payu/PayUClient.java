package com.example.paymentclient.payu;

import com.example.cinema.dto.request.PaymentRequest;
import com.example.cinema.exception.payment.AccessTokenRetrievalException;
import com.example.cinema.exception.payment.OrderRequestException;
import com.example.paymentclient.PaymentApiClient;
import com.example.token.AccessToken;
import com.example.token.TokenCache;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@Component
public class PayUClient implements PaymentApiClient {
    private final TokenCache tokenCache;
    private final PayUHttpClient payUHttpClient;
    public static final String ORDER_URL = "https://secure.snd.payu.com/api/v2_1/orders";
    public static final String TOKEN_URL = "https://secure.snd.payu.com/pl/standard/user/oauth/authorize";
    private final String clientId;
    private final String clientSecret;
    private final String merchantPosId;
    private final PayUResponseParser payUResponseParser;

    public PayUClient(TokenCache tokenCache,
                      PayUHttpClient payUHttpClient,
                      @Value("${payment.payu.clientid}") String clientId,
                      @Value("${payment.payu.secret}") String clientSecret,
                      @Value("${payment.payu.merchantposid}") String merchantPosId,
                      PayUResponseParser payUResponseParser) {
        this.tokenCache = tokenCache;
        this.payUHttpClient = payUHttpClient;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.merchantPosId = merchantPosId;
        this.payUResponseParser = payUResponseParser;
    }

    @Override
    public String pay(PaymentRequest paymentRequest) throws AccessTokenRetrievalException, OrderRequestException {
        if (tokenCache.getToken() == null) tryToRetrieveToken();
        PayUOrderBody orderBody = new PayUOrderBody(paymentRequest, merchantPosId);
        String response = sendOrder(orderBody);

        if (response == null) throw new OrderRequestException();

        return payUResponseParser.parseRedirectionUri(response);
    }

    private String sendOrder(PayUOrderBody payUOrderBody) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(tokenCache.getToken());
        return payUHttpClient.post(ORDER_URL, headers, payUOrderBody);
    }

    private void tryToRetrieveToken() throws AccessTokenRetrievalException{
        getAccessToken();
        if (tokenCache.getToken() == null) {
            throw new AccessTokenRetrievalException();
        }
    }

    @Override
    public AccessToken getAccessToken() throws AccessTokenRetrievalException {
        String response = fetchAccessToken();
        AccessToken token = payUResponseParser.parseAccessToken(response);
        tokenCache.addToken(token.token(), token.expiresIn());
        return token;
    }

    private String fetchAccessToken() {
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("grant_type", "client_credentials");
        formData.add("client_id", clientId);
        formData.add("client_secret", clientSecret);

        return payUHttpClient.postForm(TOKEN_URL, formData);
    }
}
