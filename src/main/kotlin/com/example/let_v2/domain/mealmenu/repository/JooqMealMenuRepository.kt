package com.example.let_v2.domain.mealmenu.repository

import com.example.auth.generated.jooq.enums.MealsMealType
import com.example.auth.generated.jooq.tables.MealMenus
import com.example.auth.generated.jooq.tables.Meals
import com.example.auth.generated.jooq.tables.MenuAllergies
import com.example.auth.generated.jooq.tables.Menus
import com.example.auth.generated.jooq.tables.records.MealMenusRecord
import com.example.let_v2.domain.meal.domain.MealType
import com.example.let_v2.domain.mealmenu.domain.MealMenu
import org.jooq.DSLContext
import org.jooq.impl.DSL
import org.springframework.stereotype.Repository

@Repository
class JooqMealMenuRepository(
    private val dsl: DSLContext
): MealMenuRepository{
    override fun findMonthlyMealMenu(params: Map<String, Any>): List<MealMenu> {
        val yearMonth = params["yearMonth"] as String
        val mealType = params["mealType"] as MealType
        val allergyList = params["allergyList"] as? List<Int> ?: emptyList()

        val mm = MealMenus.MEAL_MENUS
        val m = Menus.MENUS
        val ml = Meals.MEALS
        val ma = MenuAllergies.MENU_ALLERGIES

        // 조건 체인 만들기
        var condition = DSL.trueCondition()
            .and(DSL.field("DATE_FORMAT({0}, {1})", String::class.java, ml.MEAL_DATE, DSL.inline("%Y-%m")).eq(yearMonth))
            .and(ml.MEAL_TYPE.eq(mealType.toMealsMealType()))

        // JOOQ 쿼리
        val query = dsl.select(
            mm.MEAL_MENU_ID, mm.MEAL_ID, mm.MENU_ID,
            m.MENU_NAME, m.MENU_SCORE, m.LIKE_COUNT, m.DISLIKE_COUNT, m.CURRENT_RANK,
            ml.MEAL_DATE, ml.MEAL_TYPE, ml.CALORIE, ml.SCORE
        )
            .from(mm)
            .join(m).on(mm.MENU_ID.eq(m.MENU_ID))
            .join(ml).on(mm.MEAL_ID.eq(ml.MEAL_ID))
            .leftJoin(ma).on(m.MENU_ID.eq(ma.MENU_ID))
            .where(condition)
            .groupBy(mm.MEAL_MENU_ID)
            .having(
                if (allergyList.isNotEmpty()) {
                    DSL.condition(
                        "SUM(CASE WHEN {0} IN ({1}) THEN 1 ELSE 0 END) = 0",
                        ma.ALLERGY,
                        DSL.list(allergyList.map { DSL.inline(it) })
                    )
                } else {
                    DSL.noCondition()
                }
            )
            .orderBy(
                ml.MEAL_DATE.asc(),
                DSL.case_(ml.MEAL_TYPE)
                    .`when`(MealsMealType.조식, 1)
                    .`when`(MealsMealType.중식, 2)
                    .`when`(MealsMealType.석식, 3)
                    .otherwise(999)
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

    private fun MealMenusRecord.toDomain(): MealMenu {
        return MealMenu(
            id = this.mealMenuId,
            mealId = this.mealId!!,
            menuId = this.menuId!!
        )
    }
}