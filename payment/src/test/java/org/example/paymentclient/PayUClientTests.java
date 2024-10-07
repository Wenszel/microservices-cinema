package org.example.paymentclient;

import org.example.TokenCache;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
public class PayUClientTests {
    @Mock
    private TokenCache tokenCache;
    private PayUClient payUClient;
    @Test
    public void testGetAccessToken() {
        payUClient = new PayUClient(tokenCache, "460718", "22f4175da9f0f72bcce976dd8bd7504f");
        String accessToken = payUClient.getAccessToken();
        assertNotNull(accessToken);
    }
}
