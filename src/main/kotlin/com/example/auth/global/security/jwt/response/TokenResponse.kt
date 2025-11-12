package com.example.auth.global.security.jwt.response

data class TokenResponse(
    val accessToken: String,
    val refreshToken: String
)
