package com.example.let_v2.domain.allergy.error

import com.example.let_v2.global.error.CustomError
import org.springframework.http.HttpStatus

enum class AllergyError(
    override val message: String,
    override val status: Int
    ): CustomError {
    ALLERGY_NOT_FOUND( "Allergy not found", HttpStatus.NOT_FOUND.value())
}