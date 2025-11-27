package com.example.let_v2.domain.menu.repository

import com.example.auth.generated.jooq.tables.Menus
import com.example.auth.generated.jooq.tables.records.MenusRecord
import com.example.let_v2.domain.menu.domain.Menu
import org.jooq.DSLContext
import org.springframework.stereotype.Repository

@Repository
class JooqMenuRepository(
    private val dsl: DSLContext
): MenuRepository {
    override fun save(menu: Menu): Menu {
        val record = dsl.insertInto(Menus.MENUS)
            .set(Menus.MENUS.MENU_NAME, menu.name)
            .set(Menus.MENUS.MENU_SCORE, menu.score)
            .set(Menus.MENUS.LIKE_COUNT, menu.likeCount)
            .set(Menus.MENUS.DISLIKE_COUNT, menu.dislikeCount)
            .set(Menus.MENUS.CURRENT_RANK, menu.currentRank)
            .returning()
            .fetchOne()!!
        return record.toDomain()
    }

    override fun saveAll(menus: List<Menu>): List<Menu> {
        val records = menus.map { menu ->
            dsl.insertInto(Menus.MENUS)
                .set(Menus.MENUS.MENU_NAME, menu.name)
                .set(Menus.MENUS.MENU_SCORE, menu.score)
                .set(Menus.MENUS.LIKE_COUNT, menu.likeCount)
                .set(Menus.MENUS.DISLIKE_COUNT, menu.dislikeCount)
                .set(Menus.MENUS.CURRENT_RANK, menu.currentRank)
                .returning()
                .fetchOne()!!
        }
        return records.map { it.toDomain() }
    }

    override fun findAllByNameIn(names: List<String>): List<Menu> {
        return dsl.selectFrom(Menus.MENUS)
            .where(Menus.MENUS.MENU_NAME.`in`(names))
            .fetch()
            .map { it.toDomain() }
    }

    override fun findByName(name: String): Menu? {
        return dsl.selectFrom(Menus.MENUS)
            .where(Menus.MENUS.MENU_NAME.eq(name))
            .fetchOne()
            ?.toDomain()
    }

    override fun findById(id: Long): Menu? {
        return dsl.selectFrom(Menus.MENUS)
            .where(Menus.MENUS.MENU_ID.eq(id))
            .fetchOne()
            ?.toDomain()
    }

    private fun MenusRecord.toDomain(): Menu {
        return Menu(
            id = this.menuId,
            name = requireNotNull(this.menuName),
            score = this.menuScore,
            likeCount = this.likeCount,
            dislikeCount = this.dislikeCount,
            currentRank = this.currentRank
        )
    }
}