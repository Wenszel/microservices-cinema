package com.example.paymentclient;

import com.example.cinema.exception.payment.AccessTokenRetrievalException;
import com.example.cinema.exception.payment.OrderRequestException;
import com.example.paymentclient.payu.PayUHttpClient;
import com.example.token.AccessToken;
import com.example.token.TokenCache;
import com.example.paymentclient.payu.PayUClient;
import com.example.paymentclient.payu.PayUResponseParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PayUClientTests {
    private static final String CLIENT_ID = "300746";
    private static final String SECRET = "2ee86a66e5d97e3fadc400c9f19b065d";
    @Mock
    private TokenCache tokenCache;
    @Mock
    private PayUHttpClient payUHttpClient;
    @Spy
    private PayUResponseParser payUResponseParser;
    private PayUClient payUClient;

    @BeforeEach
    public void setup() {
        payUClient =
                new PayUClient(tokenCache, payUHttpClient, CLIENT_ID, SECRET, CLIENT_ID, payUResponseParser);
    }

    @Test
    public void testGetAccessToken() throws AccessTokenRetrievalException {
        // given
        String token = "test_access_token";
        int expiresIn = 43199;
        when(payUHttpClient.postForm(
                any(),
                any()
        )).thenReturn("{ " +
                "\"access_token\": \"" + token + "\", " +
                "\"token_type\": \"bearer\", " +
                "\"expires_in\": " + expiresIn + ", " +
                "\"grant_type\": \"client_credentials\" " +
                "}");

        // when
        AccessToken accessToken = payUClient.getAccessToken();

        // then
        assertEquals(accessToken.token(), token);
        assertEquals(accessToken.expiresIn(), expiresIn);
        verify(tokenCache).addToken(accessToken.token(), accessToken.expiresIn());
    }

    @Test
    public void testHttpClientReturnsNull() {
        when(payUHttpClient.postForm(
                any(),
                any()
        )).thenReturn(null);

        assertThrows(
                AccessTokenRetrievalException.class,
                () -> payUClient.getAccessToken()
        );
    }

    @Test
    public void testOrder_whenNoToken_shouldThrowError() {
        when(tokenCache.getToken()).thenReturn(null);

        assertThrows(
                AccessTokenRetrievalException.class,
                () -> payUClient.pay(null)
        );
    }

   @Test
   public void testOrder_whenApiError_shouldThrowError() {
       when(tokenCache.getToken()).thenReturn("test_token");
       when(payUHttpClient.post(
               any(),
               any(),
               any()
       )).thenReturn(null);

       assertThrows(
               OrderRequestException.class,
               () -> payUClient.pay(null)
       );
   }

   @Test
   public void testOrder_whenParseError_shouldThrowError() {
         when(tokenCache.getToken()).thenReturn("test_token");
         when(payUHttpClient.post(
                any(),
                any(),
                any()
         )).thenReturn("{ \"not_redirect_url\": \"test_uri\" }");

         assertThrows(
                OrderRequestException.class,
                () -> payUClient.pay(null)
         );
   }

    @Test
    public void testOrder_whenSuccess_shouldReturnUri() throws AccessTokenRetrievalException, OrderRequestException {
        when(tokenCache.getToken()).thenReturn("test_token");
        when(payUHttpClient.post(
                any(),
                any(),
                any()
        )).thenReturn("{ \"redirectUri\": \"test_uri\" }");

        assertEquals("test_uri", payUClient.pay(null));
    }
}
