package com.example.let_v2.domain.mealmenu.repository

import com.example.let_v2.domain.mealmenu.domain.MealMenu

interface MealMenuRepository {
    fun saveAllBatch(mealMenus: List<MealMenu>)
    fun findAllByMealIdIn(mealIds: List<Long>):List<MealMenu>
}