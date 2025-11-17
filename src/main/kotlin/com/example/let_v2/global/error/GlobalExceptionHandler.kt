package com.example.let_v2.global.error

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice


@RestControllerAdvice
class GlobalExceptionHandler {
    @ExceptionHandler(CustomException::class)
    fun handleCustomException(ex: CustomException) =
        ErrorResponse.from(ex.error)

    @ExceptionHandler(Exception::class)
    fun handleAll(ex: Exception) =
        ResponseEntity.status(500).body(ErrorResponse(500, ex.message ?: "Unknown error"))
}