package com.example.let_v2.domain.menu.repository.allergy

import com.example.auth.generated.jooq.tables.references.MENU_ALLERGIES
import com.example.let_v2.domain.allergy.Allergy
import com.example.let_v2.domain.menu.domain.MenuAllergy
import org.jooq.DSLContext
import org.springframework.stereotype.Repository

@Repository
class JooqMenuAllergyRepository(
    private val dsl: DSLContext
): MenuAllergyRepository {
    override fun saveAllBatch(menuAllergyList: List<MenuAllergy>) {
        if (menuAllergyList.isEmpty()) return

        val batch = menuAllergyList.map { ma ->
            dsl.insertInto(MENU_ALLERGIES)
                .set(MENU_ALLERGIES.MENU_ID, ma.menuId)
                .set(MENU_ALLERGIES.ALLERGY, ma.allergy.name)
                .onDuplicateKeyIgnore()
        }
        dsl.batch(batch).execute()
    }

    override fun findAllByMenuIdIn(menuIds: List<Long>): List<MenuAllergy> {
        if (menuIds.isEmpty()) return emptyList()

        return dsl.selectFrom(MENU_ALLERGIES)
            .where(MENU_ALLERGIES.MENU_ID.`in`(menuIds))
            .fetch()
            .map { record ->
                MenuAllergy(
                    menuId = record.get(MENU_ALLERGIES.MENU_ID)!!,
                    allergy = Allergy.valueOf(record.get(MENU_ALLERGIES.ALLERGY)!!)
                )
            }
    }
}