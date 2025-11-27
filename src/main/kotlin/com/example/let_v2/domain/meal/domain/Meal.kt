package com.example.let_v2.domain.meal.domain

import java.time.LocalDate

data class Meal (
    val id : Int? = null,
    val mealDate : LocalDate,
    val mealType : MealType,
    val score : Float? = 0f,
    val calories : Float,
)