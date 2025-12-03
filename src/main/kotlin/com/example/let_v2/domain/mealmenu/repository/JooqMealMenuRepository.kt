package com.example.let_v2.domain.mealmenu.repository

import com.example.auth.generated.jooq.enums.MealsMealType
import com.example.auth.generated.jooq.tables.MealMenus
import com.example.auth.generated.jooq.tables.Meals
import com.example.auth.generated.jooq.tables.MenuAllergies
import com.example.auth.generated.jooq.tables.Menus
import com.example.let_v2.domain.mealmenu.domain.MealMenu
import com.example.let_v2.domain.mealmenu.dto.MonthlyMealMenuQuery
import org.jooq.DSLContext
import org.jooq.impl.DSL
import org.springframework.stereotype.Repository
import java.time.LocalDate
import java.util.Date

@Repository
class JooqMealMenuRepository(
    private val dsl: DSLContext
): MealMenuRepository{
    override fun findMonthlyMealMenu(query: MonthlyMealMenuQuery): List<MealMenu> {
        val yearMonth = query.yearMonth
        val mealType = query.mealType
        val allergyList = query.allergyList

        val mm = MealMenus.MEAL_MENUS
        val m = Menus.MENUS
        val ml = Meals.MEALS
        val ma = MenuAllergies.MENU_ALLERGIES

        // 날짜 범위 계산
        val startDate = LocalDate.parse("$yearMonth-01")
        val endDate = startDate.plusMonths(1).minusDays(1)

        // 기본 조건
        val baseCondition = ml.MEAL_DATE.between(startDate, endDate)
            .and(ml.MEAL_TYPE.eq(mealType.toMealsMealType()))

        // 알레르기 필터링이 필요한 경우, 제외할 meal_id 조회
        val excludedMealIds = if (allergyList.isNotEmpty()) {
            dsl.select(mm.MEAL_ID)
                .from(mm)
                .join(ma).on(mm.MENU_ID.eq(ma.MENU_ID))
                .where(ma.ALLERGY.`in`(allergyList))
                .fetch(mm.MEAL_ID)
                .toSet()
        } else {
            emptySet()
        }

        // 최종 조건
        val finalCondition = if (excludedMealIds.isNotEmpty()) {
            baseCondition.and(ml.MEAL_ID.notIn(excludedMealIds))
        } else {
            baseCondition
        }

        // 메인 쿼리
        val query = dsl.select(
            ml.MEAL_ID, ml.MEAL_DATE, ml.MEAL_TYPE, ml.CALORIE,
            m.MENU_ID, m.MENU_NAME
        )
            .from(mm)
            .join(m).on(mm.MENU_ID.eq(m.MENU_ID))
            .join(ml).on(mm.MEAL_ID.eq(ml.MEAL_ID))
            .where(finalCondition)
            .orderBy(
                ml.MEAL_DATE.asc(),
                DSL.case_(ml.MEAL_TYPE)
                    .`when`(MealsMealType.조식, 1)
                    .`when`(MealsMealType.중식, 2)
                    .`when`(MealsMealType.석식, 3)
                    .otherwise(999),
                mm.MEAL_MENU_ID.asc() // 메뉴 순서 보장
            )
        return query.fetchInto(MealMenu::class.java)
    }

    override fun findDailyMealMenu(today: LocalDate): List<MealMenu> {
        val mm = MealMenus.MEAL_MENUS
        val m = Menus.MENUS
        val ml = Meals.MEALS

        val query = dsl.select(
            ml.MEAL_ID, ml.MEAL_DATE, ml.MEAL_TYPE, ml.CALORIE,
            m.MENU_ID, m.MENU_NAME
        )
            .from(
                mm
            )
            .join(m).on(mm.MENU_ID.eq(m.MENU_ID))
            .join(ml).on(mm.MEAL_ID.eq(ml.MEAL_ID))
            .where(ml.MEAL_DATE.eq(today))
            .orderBy(
                ml.MEAL_DATE.asc(),
                DSL.case_(ml.MEAL_TYPE)
                    .`when`(MealsMealType.조식, 1)
                    .`when`(MealsMealType.중식, 2)
                    .`when`(MealsMealType.석식, 3)
                    .otherwise(999),
                mm.MEAL_MENU_ID.asc() // 메뉴 순서 보장
            )
        return query.fetchInto(MealMenu::class.java)
    }

    override fun saveAllBatch(mealMenus: List<MealMenu>) {
        if (mealMenus.isEmpty()) return

        // 1. Insert 쿼리 빌더 준비
        val batchQueries = mealMenus.map { mealMenu ->
            dsl.insertInto(MealMenus.MEAL_MENUS)
                .set(MealMenus.MEAL_MENUS.MEAL_ID, mealMenu.mealId)
                .set(MealMenus.MEAL_MENUS.MENU_ID, mealMenu.menuId)
        }

        // 2. Batch 실행 (개별 ID 반환 없이 고속 삽입)
        dsl.batch(batchQueries).execute()
    }

    override fun findAllByMealIdIn(mealIds: List<Long>): List<MealMenu> {
        if (mealIds.isEmpty()) return emptyList()

        return dsl
            .selectFrom(MealMenus.MEAL_MENUS)
            .where(MealMenus.MEAL_MENUS.MEAL_ID.`in`(mealIds))
            .fetchInto(MealMenu::class.java)
    }
}