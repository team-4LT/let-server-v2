package com.example.let_v2.domain.meal.repository

import com.example.let_v2.domain.meal.domain.Meal
import com.example.let_v2.domain.meal.error.MealError
import com.example.let_v2.global.error.CustomException
import java.time.LocalDate

interface MealRepository {
    fun save(meal: Meal) : Meal
    fun findById(id: Int) : Meal?
    fun saveAll(meals: List<Meal>): List<Meal>
    fun findAllByMealDateIn(mealDates: List<LocalDate>):List<Meal>
}

fun MealRepository.findByIdOrThrow(id: Int): Meal {
    return this.findById(id) ?: throw CustomException(MealError.MEAL_NOT_FOUND)
}