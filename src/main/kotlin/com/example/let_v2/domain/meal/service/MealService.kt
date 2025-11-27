package com.example.let_v2.domain.meal.service

import com.example.let_v2.domain.allergy.Allergy
import com.example.let_v2.domain.allergy.error.AllergyError
import com.example.let_v2.domain.meal.domain.MealType
import com.example.let_v2.domain.meal.dto.response.GetMonthlyMealResponse
import com.example.let_v2.domain.meal.error.MealError
import com.example.let_v2.domain.meal.repository.MealRepository
import com.example.let_v2.domain.meal.repository.findByIdOrThrow
import com.example.let_v2.domain.mealmenu.domain.MealMenu
import com.example.let_v2.domain.mealmenu.repository.MealMenuRepository
import com.example.let_v2.domain.menu.dto.response.MenuResponse
import com.example.let_v2.domain.menu.repository.MenuRepository
import com.example.let_v2.domain.menu.repository.findByIdOrThrow
import com.example.let_v2.global.error.CustomException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Service
@Transactional(readOnly = true)
class MealService(
    private val mealRepository: MealRepository,
    private val menuRepository: MenuRepository,
    private val mealMenuRepository: MealMenuRepository
) {

    companion object {
        private val YEAR_MONTH_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM")
    }

    fun getMonthlyMenu(
        period : String,
        allergyIds: List<Int>?
    ): List<GetMonthlyMealResponse> {
        val yearMonth = LocalDate.now().format(YEAR_MONTH_FORMATTER)
        val mealType = parseMealTypeOrThrow(period)
        val safeAllergyIds = allergyIds ?: emptyList()

        // 알레르기 ID 유효성 검증
        validateAllergyIds(safeAllergyIds)

        val params = mapOf(
            "yearMonth" to yearMonth,
            "mealType" to mealType,
            "allergyList" to safeAllergyIds
        )

        val mealMenus = mealMenuRepository.findMonthlyMealMenu(params)

        // MealMenu를 Meal별로 그룹화하여 응답 생성
        return groupMealMenusByMeal(mealMenus)
    }

    /**
     * MealType 문자열을 파싱하고 유효성 검증
     */
    private fun parseMealTypeOrThrow(period: String): MealType {
        return try {
            MealType.valueOf(period.uppercase())
        } catch (e: IllegalArgumentException) {
            throw CustomException(MealError.MEAL_TYPE_NOT_FOUND)
        }
    }

    /**
     * 알레르기 ID 유효성 검증
     */
    private fun validateAllergyIds(allergyIds: List<Int>) {
        allergyIds.forEach { allergyId ->
            if (Allergy.fromId(allergyId) == null) {
                throw CustomException(AllergyError.ALLERGY_NOT_FOUND)
            }
        }
    }

    /**
     * MealMenu 리스트를 Meal별로 그룹화하여 응답 DTO 생성
     */
    private fun groupMealMenusByMeal(mealMenus: List<MealMenu>): List<GetMonthlyMealResponse> {
        val mealMap = linkedMapOf<Long, GetMonthlyMealResponse>()

        mealMenus.forEach { mealMenu ->
            val meal = mealRepository.findByIdOrThrow(mealMenu.mealId)
            val menu = menuRepository.findByIdOrThrow(mealMenu.menuId)

            // Meal이 없으면 새로 생성, 있으면 기존 것 사용
            val mealResponse = mealMap.getOrPut(meal.id!!.toLong()) {
                GetMonthlyMealResponse.of(meal)
            }

            // 메뉴 추가
            (mealResponse.menus as MutableList).add(MenuResponse.of(menu))
        }

        return mealMap.values.toList()
    }
}