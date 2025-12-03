package com.example.let_v2.domain.meal.dto.response

import com.example.let_v2.domain.meal.domain.Meal
import com.example.let_v2.domain.meal.domain.MealType
import com.example.let_v2.domain.menu.dto.response.MenuResponse
import java.time.LocalDate

data class GetDailyMealResponse(
    val mealId : Int,
    val mealDate : LocalDate,
    val mealType : MealType,
    val calories : Float,
    val menus : List<MenuResponse>
) {
    companion object {
        fun of(meal: Meal, menus: List<MenuResponse> = emptyList()): GetDailyMealResponse {
            return GetDailyMealResponse(
                mealId = meal.id!!,
                mealDate = meal.mealDate,
                mealType = meal.mealType,
                calories = meal.calories,
                menus = menus
            )
        }
    }
}