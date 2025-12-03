package com.example.let_v2.domain.meal.repository

import com.example.auth.generated.jooq.enums.MealsMealType
import com.example.auth.generated.jooq.tables.records.MealsRecord
import com.example.auth.generated.jooq.tables.references.MEALS
import com.example.let_v2.domain.meal.domain.Meal
import com.example.let_v2.domain.meal.domain.MealType
import org.jooq.DSLContext
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
class JooqMealRepository(
    private val dsl: DSLContext
) : MealRepository {

    override fun save(meal: Meal): Meal {
        val jooqMealType = meal.mealType.toJooqMealType()

        val record = dsl.insertInto(MEALS)
            .set(MEALS.MEAL_DATE, meal.mealDate)
            .set(MEALS.MEAL_TYPE, jooqMealType)
            .set(MEALS.CALORIE, meal.calories.toDouble())
            .returning()
            .fetchOne()!!
        
        return record.toDomain()
    }

    override fun findById(id: Int): Meal? {
        return dsl.selectFrom(MEALS)
            .where(MEALS.MEAL_ID.eq(id))
            .fetchOne()
            ?.toDomain()
    }

    override fun saveAll(meals: List<Meal>): List<Meal> {
        return meals.map { save(it) }
    }

    override fun findAllByMealDateIn(mealDates: List<LocalDate>): List<Meal> {
        if (mealDates.isEmpty()) return emptyList()

        return dsl
            .selectFrom(MEALS)
            .where(MEALS.MEAL_DATE.`in`(mealDates))
            .fetch()
            .map { record ->
                Meal(
                    id = record.mealId,
                    mealDate = record.mealDate!!,
                    mealType = record.mealType?.toDomainMealType()!!,
                    calories = record.calorie?.toFloat()!!
                )
            }
    }

    private fun MealType.toJooqMealType(): MealsMealType {
        return MealsMealType.valueOf(this.value)
    }

    private fun MealsMealType.toDomainMealType(): MealType {
        return MealType.entries.first { it.value == this.literal }
    }

    private fun MealsRecord.toDomain(): Meal {
        return Meal(
            id = this.mealId!!,
            mealDate = this.mealDate!!,
            mealType = this.mealType!!.toDomainMealType(),
            calories = this.calorie?.toFloat()!!
        )
    }
}