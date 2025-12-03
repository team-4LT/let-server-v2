package com.example.let_v2.domain.eater.error

import com.example.let_v2.global.error.CustomError
import org.springframework.http.HttpStatus

enum class EaterError(
    override val message: String,
    override val status: Int
): CustomError {
    INVALID_GRADE("Invalid grade value. Grade must be 1~3.", HttpStatus.BAD_REQUEST.value())
}