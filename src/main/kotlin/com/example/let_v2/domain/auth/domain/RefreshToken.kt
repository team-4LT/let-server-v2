package com.example.let_v2.domain.auth.domain

import org.springframework.data.annotation.Id
import org.springframework.data.redis.core.RedisHash
import org.springframework.data.redis.core.index.Indexed
import java.time.LocalDateTime

@RedisHash(value = "refresh_token", timeToLive = 604800) // 7일
data class RefreshToken(
    @Id
    val username: String,

    @Indexed
    val refreshToken: String,

    val expiresAt: LocalDateTime
)

