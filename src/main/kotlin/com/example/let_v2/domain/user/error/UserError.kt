package com.example.let_v2.domain.user.error

import com.example.let_v2.global.error.CustomError
import org.springframework.http.HttpStatus

enum class UserError(
    override val status: Int,
    override val message: String
) : CustomError{
    USERNAME_DUPLICATION(HttpStatus.CONFLICT.value(), "Username is already taken."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "User not found."),
    INVALID_PASSWORD(HttpStatus.UNAUTHORIZED.value(), "Invalid password."),
    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED.value(), "Invalid username or password.")
}