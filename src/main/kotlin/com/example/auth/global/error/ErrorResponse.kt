package com.example.auth.global.error

import org.springframework.http.ResponseEntity

data class ErrorResponse(
    val status: Int,
    val message: String
) {
    companion object {
        fun from(error: CustomError): ResponseEntity<ErrorResponse> =
            ResponseEntity.status(error.status).body(ErrorResponse(error.status, error.message))
    }
}