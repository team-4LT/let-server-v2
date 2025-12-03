package com.example.let_v2.domain.eater.repository

import com.example.auth.generated.jooq.tables.Eaters
import com.example.let_v2.domain.eater.domain.Eater
import org.jooq.DSLContext
import org.springframework.stereotype.Repository

@Repository
class JooqEaterRepository(
    val dsl : DSLContext
): EaterRepository {
    override fun saveAll(eaters: List<Eater>) {
        dsl.batch(
            eaters.map { eater ->
                dsl.insertInto(Eaters.EATERS)
                    .set(Eaters.EATERS.USER_ID, eater.userId)
                    .set(Eaters.EATERS.MEAL_ID, eater.mealId)
                    .set(Eaters.EATERS.EATEN, eater.eaten)
            }
        ).execute()
    }
}