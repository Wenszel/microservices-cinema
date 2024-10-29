package com.example.paymentclient.payu;

import com.example.cinema.dto.request.PaymentRequest;

import java.util.List;

public class PayUOrderBody extends PaymentRequest {
    public String merchantPosId;
    public PayUOrderBody(
            String customerIp,
            String description,
            String currencyCode,
            String totalAmount,
            List<Product> products,
            String merchantPosId
    ) {
        super(customerIp, description, currencyCode, totalAmount, products);
        this.merchantPosId = merchantPosId;
    }
    public PayUOrderBody(PaymentRequest paymentRequest, String merchantPosId) {
        this(paymentRequest.getCustomerIp(), paymentRequest.getDescription(), paymentRequest.getCurrencyCode(), paymentRequest.getTotalAmount(), paymentRequest.getProducts(), merchantPosId);
    }
    public String toString() {
        return "{\n" +
                "  \"customerIp\": \"" + getCustomerIp() + "\",\n" +
                "  \"description\": \"" + getDescription() + "\",\n" +
                "  \"currencyCode\": \"" + getCurrencyCode() + "\",\n" +
                "  \"totalAmount\": \"" + getTotalAmount() + "\",\n" +
                "  \"products\": [\n" +
                "    " + getProducts().stream().map(product -> "{\n" +
                "      \"name\": \"" + product.name() + "\",\n" +
                "      \"quantity\": \"" + product.quantity() + "\",\n" +
                "      \"price\": \"" + product.unitPrice() + "\"\n" +
                "    }").reduce((a, b) -> a + ",\n" + b).orElse("") + "\n" +
                "  ],\n" +
                "  \"merchantPosId\": \"" + merchantPosId + "\"\n" +
                "}";
    }
}
