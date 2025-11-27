package com.example.let_v2.domain.meal.domain

import com.example.auth.generated.jooq.enums.MealsMealType

enum class MealType(val value: String, val code: Int) {
    BREAKFAST("조식", 1),
    LUNCH("중식", 2),
    DINNER("석식", 3);

    fun toMealsMealType(): MealsMealType = when(this) {
        BREAKFAST -> MealsMealType.조식
        LUNCH -> MealsMealType.중식
        DINNER -> MealsMealType.석식
    }

    fun toInt(): Int = code
}