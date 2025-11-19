package com.example.let_v2.domain.auth.error

import com.example.let_v2.global.error.CustomError
import org.springframework.http.HttpStatus

enum class AuthError(
    override val message: String,
    override val status: Int
) : CustomError {
    INVALID_CLIENT_TYPE("Invalid client type. Allowed values: web, app, mobile", HttpStatus.BAD_REQUEST.value()),
    REFRESH_TOKEN_NOT_FOUND("Refresh token not found. Please provide token in cookie (web) or Authorization header (app)", HttpStatus.UNAUTHORIZED.value())
}

