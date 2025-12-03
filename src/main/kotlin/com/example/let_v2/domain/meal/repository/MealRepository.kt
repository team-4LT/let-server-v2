package com.example.let_v2.domain.meal.repository

import com.example.let_v2.domain.meal.domain.Meal
import com.example.let_v2.domain.meal.domain.MealType
import com.example.let_v2.domain.meal.error.MealError
import com.example.let_v2.global.error.CustomException
import java.time.LocalDate
import java.time.LocalDateTime


interface MealRepository {
    fun save(meal: Meal) : Meal
    fun findById(id: Int) : Meal?
    fun saveAll(meals: List<Meal>): List<Meal>
    fun findAllByMealDateIn(mealDates: List<LocalDate>):List<Meal>
}

fun MealRepository.findByIdOrThrow(id: Int): Meal {
    return this.findById(id) ?: throw CustomException(MealError.MEAL_NOT_FOUND)
}

fun MealRepository.getCurrentMeal(): Meal {
    val currentMealType = getCurrentMealType()
    val today = LocalDate.now()

    return this.findAllByMealDateIn(listOf(today))
        .firstOrNull { it.mealType == currentMealType }
        ?: throw CustomException(MealError.MEAL_NOT_FOUND)
}

private fun getCurrentMealType(): MealType {
    val hour = LocalDateTime.now().hour
    return when {
        hour < 10 -> MealType.BREAKFAST
        hour < 15 -> MealType.LUNCH
        else -> MealType.DINNER
    }
}