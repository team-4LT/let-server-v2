package com.example.let_v2.domain.mealmenu.dto

import com.example.let_v2.domain.meal.domain.MealType

data class MonthlyMealMenuQuery(
    val yearMonth: String,
    val mealType: MealType,
    val allergyList: List<Int> = emptyList()
)
