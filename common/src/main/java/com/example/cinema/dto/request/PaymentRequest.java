package com.example.cinema.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

import java.util.List;

@AllArgsConstructor
@Data
public class PaymentRequest {
    @NonNull
    private String customerIp;
    @NonNull
    private String description;
    @NonNull
    private String currencyCode;
    @NonNull
    private String totalAmount;
    @NonNull
    private List<Product> products;

    public record Product(String name, String quantity, String unitPrice) {}
}
