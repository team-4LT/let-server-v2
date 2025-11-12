package com.example.auth.global.security.jwt.properties

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "jwt")
data class JwtProperties(
    val secretKey: String,
    val accessTokenExpiration: Long,
    val refreshTokenExpiration: Long
)