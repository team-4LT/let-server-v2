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