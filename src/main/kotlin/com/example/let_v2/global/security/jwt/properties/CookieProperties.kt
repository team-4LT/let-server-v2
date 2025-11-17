package com.example.let_v2.global.security.jwt.properties

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "cookie")
data class CookieProperties(
    val accessTokenMaxAge: Long,
    val refreshTokenMaxAge: Long
)