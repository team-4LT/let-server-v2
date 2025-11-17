package com.example.let_v2.domain.user.repository.allergy

import com.example.auth.generated.jooq.tables.references.USER_ALLERGIES
import com.example.let_v2.domain.user.domain.UserAllergy
import org.jooq.DSLContext
import org.springframework.stereotype.Repository

@Repository
class JooqUserAllergyRepository(
    private val dsl: DSLContext
) : UserAllergyRepository {
    override fun saveAll(userAllergies: List<UserAllergy>) {
        val batch = userAllergies.map { ua ->
            dsl.insertInto(USER_ALLERGIES)
                .set(USER_ALLERGIES.USER_ID, ua.userId)
                .set(USER_ALLERGIES.ALLERGY, ua.allergy.name)
        }
        dsl.batch(batch).execute()
    }
}