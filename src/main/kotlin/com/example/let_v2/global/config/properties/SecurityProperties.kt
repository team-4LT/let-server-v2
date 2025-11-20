package com.example.let_v2.global.config.properties

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "security")
data class SecurityProperties(
    val appSecret: String,

    val dummyPasswordHash: String
)