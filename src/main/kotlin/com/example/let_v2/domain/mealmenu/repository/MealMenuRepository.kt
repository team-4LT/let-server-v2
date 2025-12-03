package com.example.let_v2.domain.mealmenu.repository

import com.example.let_v2.domain.mealmenu.domain.MealMenu
import com.example.let_v2.domain.mealmenu.dto.MonthlyMealMenuQuery
import java.time.LocalDate
import java.util.Date

interface MealMenuRepository {
    fun findMonthlyMealMenu(query: MonthlyMealMenuQuery): List<MealMenu>
    fun saveAllBatch(mealMenus: List<MealMenu>)
    fun findAllByMealIdIn(mealIds: List<Long>):List<MealMenu>
    fun findDailyMealMenu(today: LocalDate): List<MealMenu>
}