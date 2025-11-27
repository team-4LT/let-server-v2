package com.example.let_v2.domain.meal.dto

import com.example.let_v2.domain.meal.domain.MealType
import com.example.let_v2.domain.mealmenu.dto.MenuInfo

data class MealInfo (
    val mealDate: String,
    val mealType: MealType,
    val calories: Float,
    val menus: List<MenuInfo>
)