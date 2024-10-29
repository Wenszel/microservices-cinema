package com.example.cinema.dto.response;

public record AccessTokenResponse(String accessToken,
                                  String tokenType,
                                  Integer expiresIn,
                                  String refreshToken,
                                  String grantType) {}
