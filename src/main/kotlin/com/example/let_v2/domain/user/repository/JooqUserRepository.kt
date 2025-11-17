package com.example.let_v2.domain.user.repository

import com.example.auth.generated.jooq.tables.Users.Companion.USERS
import com.example.auth.generated.jooq.tables.records.UsersRecord
import com.example.auth.generated.jooq.enums.UsersRole
import com.example.let_v2.domain.user.domain.User
import com.example.let_v2.domain.user.domain.UserRole
import org.jooq.DSLContext
import org.springframework.stereotype.Repository

@Repository
class JooqUserRepository(private val dsl: DSLContext) : UserRepository {
    override fun save(user: User): User {
        val record = dsl.insertInto(USERS)
            .columns(
                USERS.USERNAME,
                USERS.PASSWORD,
                USERS.ROLE,
                USERS.STUDENT_ID,
                USERS.REAL_NAME
            )
            .values(
                user.name,
                user.password,
                UsersRole.valueOf(user.role.name),
                user.studentId.toLong(),
                user.realName
            )
            .returning()
            .fetchOne()!!

        return record.toDomain()
    }

    override fun findByName(name: String): User? {
        return dsl.selectFrom(USERS)
            .where(USERS.USERNAME.eq(name))
            .fetchOne()
            ?.toDomain()
    }

    override fun existsByName(name: String): Boolean {
        return dsl.fetchExists(
            dsl.selectFrom(USERS)
                .where(USERS.USERNAME.eq(name))
        )
    }


    private fun UsersRecord.toDomain(): User {
        return User(
            id = this.userId,
            name = this.username!!,
            password = this.password!!,
            role = this.role?.let { UserRole.valueOf(it.name) } ?: UserRole.STUDENT,
            studentId = this.studentId!!.toInt(),
            realName = this.realName!!
        )
    }
}