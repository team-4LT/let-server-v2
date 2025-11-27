package com.example.let_v2.domain.meal.error

import com.example.let_v2.global.error.CustomError
import org.springframework.http.HttpStatus

enum class MealError(
    override val message: String,
    override val status: Int
) : CustomError{
    MEAL_NOT_FOUND("Meal not found.", HttpStatus.NOT_FOUND.value()),
    MEAL_TYPE_NOT_FOUND("Meal type not found.", HttpStatus.NOT_FOUND.value()),
}