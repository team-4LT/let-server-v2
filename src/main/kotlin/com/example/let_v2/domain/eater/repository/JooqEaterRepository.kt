package com.example.let_v2.domain.eater.repository

import com.example.auth.generated.jooq.tables.Eaters
import com.example.auth.generated.jooq.tables.Users
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

    override fun findByGrade(grade: Int): List<Eater> {
        val e = Eaters.EATERS
        val u = Users.USERS

        val minId = grade * 1000
        val maxId = (grade + 1) * 1000

        return dsl.select(e.EATER_ID, e.USER_ID, e.MEAL_ID, e.EATEN)
            .from(e)
            .join(u).on(e.USER_ID.eq(u.USER_ID))
            .where(u.STUDENT_ID.between(minId, maxId-1))
            .orderBy(u.STUDENT_ID.desc())
            .fetch()
            .map { record ->
                Eater(
                    id = record.get(e.EATER_ID),
                    userId = record.get(e.USER_ID)!!,
                    mealId = record.get(e.MEAL_ID)!!,
                    eaten = record.get(e.EATEN)!!
                )
            }
    }
}