package com.example.let_v2.global.ratelimit.error

import com.example.let_v2.global.error.CustomError
import org.springframework.http.HttpStatus

enum class RateLimitError(
    override val message: String,
    override val status: Int
) : CustomError {
    RATE_LIMIT_EXCEEDED("Rate limit exceeded. Please try again later.", HttpStatus.TOO_MANY_REQUESTS.value())
}