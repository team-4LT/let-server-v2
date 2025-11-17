package com.example.let_v2.global.security.jwt.response

data class TokenResponse(
    val accessToken: String,
    val refreshToken: String
)
