package com.example.let_v2.domain.meal.service

import com.example.let_v2.domain.allergy.Allergy
import com.example.let_v2.domain.allergy.error.AllergyError
import com.example.let_v2.domain.meal.domain.Meal
import com.example.let_v2.domain.meal.domain.MealType
import com.example.let_v2.domain.meal.dto.response.GetDailyMealResponse
import com.example.let_v2.domain.meal.dto.response.GetMonthlyMealResponse
import com.example.let_v2.domain.meal.error.MealError
import com.example.let_v2.domain.meal.repository.MealRepository
import com.example.let_v2.domain.meal.repository.findByIdOrThrow
import com.example.let_v2.domain.mealmenu.domain.MealMenu
import com.example.let_v2.domain.mealmenu.dto.MonthlyMealMenuQuery
import com.example.let_v2.domain.mealmenu.repository.MealMenuRepository
import com.example.let_v2.domain.menu.dto.response.MenuResponse
import com.example.let_v2.domain.menu.repository.MenuRepository
import com.example.let_v2.domain.menu.repository.findByIdOrThrow
import com.example.let_v2.global.error.CustomException
import com.example.let_v2.global.util.toLocalDate
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Date

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

        val query = MonthlyMealMenuQuery(
            yearMonth = yearMonth,
            mealType = mealType,
            allergyList = safeAllergyIds
        )

        return mealMenuRepository.findMonthlyMealMenu(query)
            .toMealResponses(mealRepository,menuRepository,GetMonthlyMealResponse::of)
    }

    fun getDailyMenu(
        today : Date
    ): List<GetDailyMealResponse> {
        val date : LocalDate = today.toLocalDate()
        return mealMenuRepository.findDailyMealMenu(date)
            .toMealResponses(mealRepository,menuRepository,GetDailyMealResponse::of)
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
}

private fun <T> List<MealMenu>.toMealResponses(
    mealRepository: MealRepository,
    menuRepository: MenuRepository,
    mapper: (Meal, List<MenuResponse>) -> T
): List<T> {
    return this.groupBy { it.mealId }
        .map { (mealId, mealMenuList) ->
            val meal = mealRepository.findByIdOrThrow(mealId)
            val menus = mealMenuList.map {
                MenuResponse.of(menuRepository.findByIdOrThrow(it.menuId))
            }
            mapper(meal, menus)
        }
}