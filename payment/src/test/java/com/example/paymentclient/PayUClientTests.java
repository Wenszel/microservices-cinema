package org.example.paymentclient;

import com.example.cinema.exception.payment.AccessTokenRetrievalException;
import org.example.token.AccessToken;
import org.example.token.TokenCache;
import org.example.paymentclient.payu.PayUClient;
import org.example.paymentclient.payu.PayUResponseParser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
public class PayUClientTests {
    private static final String CLIENT_ID = "460718";
    private static final String SECRET = "22f4175da9f0f72bcce976dd8bd7504f";
    @Mock
    private TokenCache tokenCache;
    @Mock
    private PayUResponseParser payUResponseParser;
    private PayUClient payUClient;
    @Test
    public void testGetAccessToken() throws AccessTokenRetrievalException {
        payUClient =
                new PayUClient(tokenCache, CLIENT_ID, SECRET, payUResponseParser);
        AccessToken accessToken = payUClient.getAccessToken();
        assertNotNull(accessToken);
    }
}
